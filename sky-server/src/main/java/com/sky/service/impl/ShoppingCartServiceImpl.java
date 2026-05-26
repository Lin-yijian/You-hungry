package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;


    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {

        //先判断数据库中是否有该用户的此类购物车中的商品
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,cart);
        //TODO 这个存在问题，比如userId 为空的

        Long userId = BaseContext.getCurrentId();
        if(userId ==null){
            System.out.println("8*******************************************************************");
        }else{
            System.out.println("dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
        }
        cart.setUserId(userId);

        List<ShoppingCart> list= shoppingCartMapper.list(cart);

        if(list != null && list.size()>0){
            ShoppingCart shoppingCart = list.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            shoppingCartMapper.update(shoppingCart);
        }
        else{
            if(cart.getDishId() !=null){
                Dish dish = dishMapper.getById(cart.getDishId());
                cart.setAmount(dish.getPrice());
                cart.setImage(dish.getImage());
                cart.setName(dish.getName());
            }
            else{
                Setmeal setmeal = setmealMapper.getById(cart.getSetmealId());
                cart.setAmount(setmeal.getPrice());
                cart.setImage(setmeal.getImage());
                cart.setName(setmeal.getName());
            }

            cart.setNumber(1);
            cart.setCreateTime(LocalDateTime.now());
            //若该用户的购物车有该商品，则直接修改信息就可以了
            shoppingCartMapper.insert(cart);
        }




    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        return shoppingCartMapper.list(shoppingCart);

    }

    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        shoppingCartMapper.clean(shoppingCart);
        return;
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,cart);
        cart.setUserId(BaseContext.getCurrentId());
        shoppingCartMapper.deleteOne(cart);
        return;

    }



}
