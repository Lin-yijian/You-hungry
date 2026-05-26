package com.yh.service;

import com.yh.annotation.AutoFill;
import com.yh.dto.DishDTO;
import com.yh.dto.DishPageQueryDTO;
import com.yh.entity.Dish;
import com.yh.result.PageResult;
import com.yh.vo.DishVO;

import java.util.List;

public interface DishService {
    void saveWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);


    void deleteById(List<Long> ids);

    DishVO getByIdWithFlavor(Long id);


    void update(DishDTO dishDTO);

    List<Dish> list(Integer categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);


    void startOrStop(Integer status, Long id);
}
