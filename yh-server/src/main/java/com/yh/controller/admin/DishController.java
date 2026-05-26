package com.yh.controller.admin;

import com.github.pagehelper.PageHelper;
import com.yh.dto.DishDTO;
import com.yh.dto.DishPageQueryDTO;
import com.yh.entity.Dish;
import com.yh.result.PageResult;
import com.yh.result.Result;
import com.yh.service.DishService;
import com.yh.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    //redis 缓存，是为了清理缓存数据
    @Autowired
    private RedisTemplate redisTemplate;

    public final  static String DELETE_CACHE = "dish_*";

    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        String key = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(key);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);

        return Result.success(pageResult);

    }


    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除：{}", ids);

        dishService.deleteById(ids);

        cleanCache(DELETE_CACHE);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.update(dishDTO);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache(DELETE_CACHE);
        return Result.success();
    }

    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(status, id);

        //将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache(DELETE_CACHE);

        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Dish>> list(Integer categoryId){


        log.info("查询分类菜品列表：{}", categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /*
    清理缓存数据方法
     */
    public void cleanCache(String pattern){
        Set set = redisTemplate.keys(pattern);
        redisTemplate.delete(set);
    }


}
