package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingcartMapper shoppingcartMapper;
    @Autowired
    private AddressbookMapper addressbookMapper;


    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AddressbookMapper addressBookMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;


    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //校验业务异常
        //查看是否有地址
        AddressBook addressBook = addressbookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //查看购物车是否为空
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();
        List<ShoppingCart> list = shoppingcartMapper.list(shoppingCart);
        if (list == null || list.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //插入订单数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(addressBook.getProvinceName()
                + addressBook.getCityName()
                + addressBook.getDistrictName()
                + addressBook.getDetail());
        orders.setUserId(BaseContext.getCurrentId());

        orderMapper.insert(orders);

        //插入订单详细数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for(ShoppingCart cart :list){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }

        orderDetailMapper.insert(orderDetailList);
        //清空用户购物车
        shoppingcartMapper.delete(BaseContext.getCurrentId());
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
        return orderSubmitVO;
    }





    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);
/*
        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;

 */
        paySuccess(ordersPaymentDTO.getOrderNumber());
        String orderNumber = ordersPaymentDTO.getOrderNumber(); //订单号
        Long orderid = orderMapper.getorderId(orderNumber);//根据订单号查主键

        JSONObject jsonObject = new JSONObject();//本来没有2
        jsonObject.put("code", "ORDERPAID"); //本来没有3
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        //为替代微信支付成功后的数据库订单状态更新，多定义一个方法进行修改
        Integer OrderPaidStatus = Orders.PAID; //支付状态，已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED; //订单状态，待接单
        //发现没有将支付时间 check_out属性赋值，所以在这里更新
        LocalDateTime check_out_time = LocalDateTime.now();
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, orderid);

        return vo;  //  修改支付方法中的代码
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
        //websocket
        Map map = new HashMap();
        map.put("type",1);
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号:" + outTradeNo);
        String jsonString = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }

    public OrderVO orderDetail(Long id) {
        Orders orders = orderMapper.getById(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        List<OrderDetail> list = orderDetailMapper.getByOrderId(id);
        orderVO.setOrderDetailList(list);
        return orderVO;
    }

    public PageResult orderList(int page,int pageSize,Integer status) {
        PageHelper.startPage(page, pageSize);
        OrdersPageQueryDTO dto = new OrdersPageQueryDTO();
        dto.setUserId(BaseContext.getCurrentId());
        dto.setStatus(status);
        Page<Orders> page1 = orderMapper.orderList(dto);
        List<OrderVO> list = new ArrayList<>();
        if (page1 != null && page1.getTotal() > 0) {
            for (Orders orders : page1) {
                List<OrderDetail> details = orderDetailMapper.getByOrderId(orders.getId());
                OrderVO vo = new OrderVO();
                BeanUtils.copyProperties(orders, vo);
                vo.setOrderDetailList(details);
                list.add(vo);
            }
        }
        return new PageResult(page1.getTotal(), list);
    }

    public void cancel(Long id) {
        orderMapper.cancel(Orders.CANCELLED,id);
    }

    public void orderAgain(Long id) {
        Long userId = BaseContext.getCurrentId();

        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x ->{
                    ShoppingCart shoppingCart = new ShoppingCart();
                    // 将原订单详情里面的菜品信息重新复制到购物车对象中
                    BeanUtils.copyProperties(x, shoppingCart, "id");
                    shoppingCart.setUserId(userId);
                    shoppingCart.setCreateTime(LocalDateTime.now());
                    return shoppingCart;
                }).collect(Collectors.toList());
        shoppingcartMapper.insertBatch(shoppingCartList);
    }

    //------------------------------------------------------------------------------------------------------------
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> ordersPage = orderMapper.pageQuery(ordersPageQueryDTO);
        // 部分订单状态，需要额外返回订单菜品信息，将Orders转化为OrderVO
        List<OrderVO> orderVOList = getOrderVOList(ordersPage);

        return new PageResult(ordersPage.getTotal(), orderVOList);
    }



    private List<OrderVO> getOrderVOList(Page<Orders> page) {
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();

        List<Orders> ordersList = page.getResult();
        if (!CollectionUtils.isEmpty(ordersList)) {
            for (Orders orders : ordersList) {
                // 将共同字段复制到OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                String orderDishes = getOrderDishesStr(orders);

                // 将订单菜品信息封装到orderVO中，并添加到orderVOList
                orderVO.setOrderDishes(orderDishes);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param orders
     * @return
     */
    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orders.getId());

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }

    public OrderStatisticsVO orderstatistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(orderMapper.orderStatistics(Orders.CONFIRMED));
        orderStatisticsVO.setToBeConfirmed(orderMapper.orderStatistics(Orders.TO_BE_CONFIRMED));
        orderStatisticsVO.setDeliveryInProgress(orderMapper.orderStatistics(Orders.DELIVERY_IN_PROGRESS));
        return orderStatisticsVO;
    }

    public void cancelAdmin(OrdersCancelDTO ordersCancelDTO) {
        orderMapper.cancelAdmin(ordersCancelDTO.getCancelReason(),ordersCancelDTO.getId(),Orders.CANCELLED);
    }

    public void orderRejection(OrdersRejectionDTO ordersRejectionDTO) {
        orderMapper.orderRejection(ordersRejectionDTO.getRejectionReason(),ordersRejectionDTO.getId(),Orders.CANCELLED);
    }

    public void orderConfirm(OrdersConfirmDTO ordersConfirmDTO) {
        ordersConfirmDTO.setStatus(Orders.CONFIRMED);
        orderMapper.orderConfirm(ordersConfirmDTO.getId(),ordersConfirmDTO.getStatus());
    }

    public void orderDelevery(Long id) {
        orderMapper.orderDelevery(id,Orders.DELIVERY_IN_PROGRESS);
    }

    public void orderComplete(Long id) {
        orderMapper.orderComplete(id,Orders.COMPLETED);
    }

    public void reminder(Long id) {

        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //websocket
        Map map = new HashMap();
        map.put("type", 2);
        map.put("orderId",id);
        map.put("content","订单号"+ orders.getNumber());
        webSocketServer.sendToAllClient(JSONObject.toJSONString(map));
    }

}
