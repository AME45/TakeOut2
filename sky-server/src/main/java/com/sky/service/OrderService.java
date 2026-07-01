package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    OrderVO orderDetail(Long id);

    PageResult orderList(int page, int pageSize, Integer status);

    void cancel(Long id);

    void orderAgain(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderStatisticsVO orderstatistics();

    void cancelAdmin(OrdersCancelDTO ordersCancelDTO);

    void orderRejection(OrdersRejectionDTO ordersRejectionDTO);

    void orderConfirm(OrdersConfirmDTO ordersConfirmDTO);

    void orderDelevery(Long id);

    void orderComplete(Long id);

}
