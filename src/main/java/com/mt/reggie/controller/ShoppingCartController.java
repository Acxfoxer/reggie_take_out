package com.mt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mt.reggie.common.BaseContext;
import com.mt.reggie.common.R;
import com.mt.reggie.entity.ShoppingCart;
import com.mt.reggie.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
@Api(tags = "购物车接口")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        //创建条件构造器
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        //根据userId查询
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        //添加排序条件,根据创造时间升序
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lqw);
        return R.success(list);
    }

    //添加菜品到购物车功能
    @ApiOperation("添加菜品到购物车功能")
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //控制台输出数据
        log.info("ShoppingCart:{}",shoppingCart);
        //添加用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询当前用户id购物车是否在购物车中存在
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,userId);
        Long dishId = shoppingCart.getDishId();
        if(dishId !=null){
            lqw.eq(ShoppingCart::getDishId, dishId);
        }else {
            lqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询购物车中是否存在
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(lqw);
        if(shoppingCartServiceOne!=null){
            Integer number = shoppingCartServiceOne.getNumber();
            shoppingCartServiceOne.setNumber(number +1);
            shoppingCartService.updateById(shoppingCartServiceOne);
        }else {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCartServiceOne=shoppingCart;
        }
        return R.success(shoppingCartServiceOne);
    }

    //清空购物车
    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public R<String> clean(){
        //创建条件构造器
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        boolean flag = shoppingCartService.remove(lqw);
        return flag?R.success("清空购物车成功"):R.error("清空购物车失败");
    }

    //减少菜或者套餐数量
    @ApiOperation("减少菜或者套餐数量")
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //创建查询条件创造器
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        lqw.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(lqw);
        //创建更新条件构造器
        LambdaUpdateWrapper<ShoppingCart> luw=new LambdaUpdateWrapper<>();
        luw.set(ShoppingCart::getNumber,shoppingCartServiceOne.getNumber()-1);
        boolean flag = shoppingCartService.update(luw);
        return flag?R.success(shoppingCartServiceOne):R.error("失败");
    }
}
