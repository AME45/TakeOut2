package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    @Select("select * from setmeal where id = #{Id}")
    Setmeal selectById(Long id);

    Page<SetmealVO> pageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO);



    @AutoFill(value = OperationType.INSERT)
    void addSetmeal(Setmeal setmeal);

    void deleteSetmeal(List<Integer> ids);
}
