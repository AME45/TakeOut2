package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    public PageResult PageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> setmeals = setmealMapper.pageSetmeal(setmealPageQueryDTO);
        long total = setmeals.getTotal();
        List<SetmealVO> setmealList = setmeals.getResult();
        return new PageResult(total,setmealList);
    }

    @Transactional
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        List<SetmealDish> setmealDish = setmealDTO.getSetmealDishes();

        setmealMapper.addSetmeal(setmeal);

        for (SetmealDish setmealDish1 : setmealDish) {
            setmealDish1.setSetmealId(setmeal.getId());
        }
        setmealDishMapper.addSetmeal(setmealDish);
    }


    public void deleteSetmeal(List<Integer> ids) {
        setmealMapper.deleteSetmeal(ids);
    }

    public SetmealVO selectById(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        List<SetmealDish> setmealDish = setmealDishMapper.selectBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDish);
        return setmealVO;
    }

    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        setmealMapper.startOrStop(setmeal);
    }

    @Transactional
    public void update(SetmealDTO setmealDTO) {
        List<SetmealDish> setmealDish = setmealDTO.getSetmealDishes();
        if(setmealDish.size()>0){
            setmealDishMapper.deleteSetmealDishById(setmealDTO.getId());
            for (SetmealDish setmealDish1 : setmealDish) {
                setmealDish1.setSetmealId(setmealDTO.getId());
            }
            setmealDishMapper.addSetmeal(setmealDish);
        }
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
    }
}
