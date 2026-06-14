package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void addDishwithFlavor(DishDTO dishDTO);

    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    void delect(List<Long> ids);

    void update(DishDTO dishDTO);

    DishVO selectById(Long id);

    List<Dish> selectAllDishByCategoryId(Integer categoryId);

    List<DishVO> selectDishWithFlavorByCategoryId(Long categoryId);

    void StartOrStop(Integer status, Long id);
}
