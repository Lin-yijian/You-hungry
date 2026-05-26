package com.yh.service;

import com.yh.dto.SetmealDTO;
import com.yh.dto.SetmealPageQueryDTO;
import com.yh.entity.Setmeal;
import com.yh.result.PageResult;
import com.yh.vo.DishItemVO;
import com.yh.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void saveWithDish(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteById(List<Long> ids);


    void startOrStop(Integer status, Long id);

    SetmealVO getByIdWithDish(Long id);

    void update(SetmealDTO setmealDTO);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
