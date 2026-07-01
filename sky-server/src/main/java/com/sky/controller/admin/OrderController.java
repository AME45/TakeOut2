package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "管理端订单管理相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    @ApiOperation("各状态订单数量")
    public Result<OrderStatisticsVO> orderStatistics(){
        OrderStatisticsVO orderStatisticsVO = orderService.orderstatistics();
        return Result.success(orderStatisticsVO);
    }

    public Result<String> getOrderId(){
        return Result.success();
    }

    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result<String> orderCancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.cancelAdmin(ordersCancelDTO);
        return Result.success();
    }
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result<String> orderRejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.orderRejection(ordersRejectionDTO);
        return Result.success();
    }
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<String> orderConfirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.orderConfirm(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result<String> orderDelevery(@PathVariable Long id){
        orderService.orderDelevery(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result<String> orderComplete(@PathVariable Long id){
        orderService.orderComplete(id);
        return Result.success();
    }

    @GetMapping("/details/{id}")
    @ApiOperation("订单详细")
    public Result<OrderVO> getOrderById(@PathVariable Long id){
        OrderVO orderVO = orderService.orderDetail(id);
        return Result.success(orderVO);
    }
}
