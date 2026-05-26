package com.yh.controller.user;


import com.yh.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
public class ShopController {

    private static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping("/status")
    public Result<Integer> getShopStatus(){
        //TODO 获取营业状态
        Integer status = 1;
         status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取营业状态为{}",status ==1?"营业中":"打样");

        return Result.success(status);
    }
}
