package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {


    PageResult PageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO);

    void addSetmeal(SetmealDTO setmealDTO);

    void deleteSetmeal(List<Integer> id);

    SetmealVO selectById(Long id);

    void startOrStop(Integer status, Long id);

    void update(SetmealDTO setmealDTO);

    List<Setmeal> selectByCategoryId(Long categoryId);

    List<DishItemVO> getDishItemById(Long id);
}
