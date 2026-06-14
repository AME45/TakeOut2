package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealMapper {

    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @Select("select * from setmeal where id = #{Id}")
    Setmeal selectById(Long id);

    Page<SetmealVO> pageSetmeal(SetmealPageQueryDTO setmealPageQueryDTO);



    @AutoFill(value = OperationType.INSERT)
    void addSetmeal(Setmeal setmeal);

    void deleteSetmeal(List<Integer> ids);

    @AutoFill(value = OperationType.UPDATE)
    @Update("update setmeal set status=#{status},update_time=#{updateTime},update_user=#{updateUser} where id = #{id}")
    void startOrStop(Setmeal setmeal);

    void update(Setmeal setmeal);


    List<Setmeal> selectByCategoryId(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
}
