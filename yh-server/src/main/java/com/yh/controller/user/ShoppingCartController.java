package com.yh.controller.user;


import com.yh.dto.ShoppingCartDTO;
import com.yh.entity.ShoppingCart;
import com.yh.result.Result;
import com.yh.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){


        shoppingCartService.addShoppingCart(shoppingCartDTO);

        return Result.success();

    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> showShoppingCart(){

        List<ShoppingCart> list = shoppingCartService.showShoppingCart();

        return Result.success(list);


    }

    @DeleteMapping("/clean")
    public Result cleanShoppingCart(){
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    @PostMapping("/sub")
    public Result subShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){

        shoppingCartService.subShoppingCart(shoppingCartDTO);
        return Result.success();
    }

}
