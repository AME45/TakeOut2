package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 获取orderid的mapper层方法，写在OrderMapper.java文件下
     * @param orderNumber
     * @return
     */
    @Select("select * from orders where number=#{orderNumber}")
    Long getorderId (String orderNumber);


    /**
     * 用于替换微信支付更新数据库状态的问题
     * @param orderStatus
     * @param orderPaidStatus
     */
    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where id = #{id}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time, Long id);


    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);


    Page<Orders> orderList(OrdersPageQueryDTO ordersPageQueryDTO);

    @Update("update orders set status = #{status} where id = #{id} ")
    void cancel(Integer status, Long id);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select count(*) from orders where status = #{status}")
    Integer orderStatistics(Integer status);

    @Update("update orders set cancel_reason = #{cancelReason},status = #{status} where id = #{id} ")
    void cancelAdmin(String cancelReason, Long id,Integer status);

    @Update("update orders set rejection_reason = #{rejectionReason},status = #{status} where id = #{id}")
    void orderRejection(String rejectionReason, Long id, Integer status);

    @Update("update orders set status = #{status} where id = #{id}")
    void orderConfirm(Long id, Integer status);

    @Update("update orders set status = #{status} where id = #{id}")
    void orderDelevery(Long id, Integer status);

    @Update("update orders set status = #{status} where id = #{id}")
    void orderComplete(Long id, Integer status);

    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime orderTime);


    Double sumbyMap(Map map);


    Integer countbyMap(Map map);

    List<GoodsSalesDTO> saleStatictis(LocalDateTime begin, LocalDateTime end);
}


