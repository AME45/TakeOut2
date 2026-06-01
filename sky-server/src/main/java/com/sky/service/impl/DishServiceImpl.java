package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.FlavorMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private FlavorMapper flavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Transactional
    public void addDishwithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.save(dish);

        Long id = dish.getId();
        //口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor f : flavors) {
                f.setDishId(id);
            }
            flavorMapper.save(flavors);
        }

    }

    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> dish = dishMapper.select(dishPageQueryDTO);
        long total = dish.getTotal();
        List<DishVO> result = dish.getResult();
        return new PageResult(total,result);
    }

    @Transactional
    public void delect(List<Long> ids) {
        //是否存在起售中的菜品
        for(Long id:ids){
            Dish dish = dishMapper.getById(id);
            if(dish != null){
                if(dish.getStatus() == StatusConstant.ENABLE){
                    throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
                }
            }
        }
        //是否存在被套餐关联的菜品
        List<Long> setmealIds = setmealDishMapper.getmealIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品数据
        dishMapper.delete(ids);
        //删除菜品关联的口味数据
        flavorMapper.deleteByDishId(ids);
    }

    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        flavorMapper.deleteByDishId(Collections.singletonList(dish.getId()));

        List<DishFlavor> dishFlavor = dishDTO.getFlavors();
        if (dishFlavor != null && dishFlavor.size() > 0) {
            for(DishFlavor f : dishFlavor){
                f.setDishId(dish.getId());}
            }
            flavorMapper.save(dishFlavor);
        }

    public DishVO selectById(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> dishFlavor =  flavorMapper.getById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavor);
        return dishVO;
    }

    public List<Dish> selectAllDishByCategoryId(Integer categoryId) {
        return dishMapper.selectAllDishByCategoryId(categoryId);
    }

    public List<DishVO> selectDishWithFlavorByCategoryId(Integer categoryId) {
        List<Dish> dish = dishMapper.selectDishWithFlavorByCategoryId(categoryId);
        List<DishVO> dishVOS = new ArrayList<>();
        for(Dish d:dish){
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);
            dishVO.setFlavors(flavorMapper.getById(d.getId()));
            dishVOS.add(dishVO);
        }
        return dishVOS;
    }
}

