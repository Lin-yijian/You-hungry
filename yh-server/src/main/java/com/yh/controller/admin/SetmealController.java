package com.yh.controller.admin;

import com.yh.dto.SetmealDTO;
import com.yh.dto.SetmealPageQueryDTO;
import com.yh.result.PageResult;
import com.yh.result.Result;
import com.yh.service.SetmealService;
import com.yh.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    /**
     * 新增套餐
     */
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO){

        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){

        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @DeleteMapping
    public Result deleteById(@RequestParam List<Long> ids){
        setmealService.deleteById(ids);

        return Result.success();
    }

    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        setmealService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据id查询
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id){

        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }

    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setmealService.update(setmealDTO);
        return Result.success();
    }

}
