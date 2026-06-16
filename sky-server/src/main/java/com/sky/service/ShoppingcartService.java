package com.sky.service;


import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingcartService {
    void addCart(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> selectShoppingCart();

    void deleteShoppingCart();

    void deleteOneShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
