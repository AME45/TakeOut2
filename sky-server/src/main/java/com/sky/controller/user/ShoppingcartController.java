package com.sky.controller.user;


import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingcartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "C端购物车相关接口")
@Slf4j
public class ShoppingcartController {
    @Autowired
    private ShoppingcartService shoppingcartService;

    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result addCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingcartService.addCart(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询购物车")
    public Result<List<ShoppingCart>> selectShoppingCart() {
        List<ShoppingCart> list = shoppingcartService.selectShoppingCart();
        return Result.success(list);
    }

    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result deleteShoppingCart() {
        shoppingcartService.deleteShoppingCart();
        return Result.success();
    }

    @PostMapping("/sub")
    @ApiOperation("删除一个菜品/套餐")
    public Result deleteOneShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingcartService.deleteOneShoppingCart(shoppingCartDTO);
        return Result.success();
    }
}
