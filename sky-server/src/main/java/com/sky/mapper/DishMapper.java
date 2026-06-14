package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {


    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer selectDishBycategoryId(Long categoryId);


    @AutoFill(value = OperationType.INSERT)
    void save(Dish dish);


    Page<DishVO> select(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> selectAllDishByCategoryId(Integer categoryId);

    List<Dish> selectDishWithFlavorByCategoryId(Dish dish);

    @Update("update dish set status = #{status} where id = #{id}")
    @AutoFill(value = OperationType.UPDATE)
    void StartOrStop(Dish dish);
}
