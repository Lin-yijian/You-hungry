package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**\
     * 根据菜品id查询套餐id
     * @param dishIds
     * @return
     */
    List<Long> getByDishId(List<Long> dishIds);

    void insertBatch(List<SetmealDish> setmealDishes);

    void deleteBySetmealId(List<Long> ids);

    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySteamId(Long id);
}
