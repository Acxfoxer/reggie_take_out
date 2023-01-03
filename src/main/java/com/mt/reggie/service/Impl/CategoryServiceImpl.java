package com.mt.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mt.reggie.common.CustomException;
import com.mt.reggie.entity.Category;
import com.mt.reggie.entity.Dish;
import com.mt.reggie.entity.Setmeal;
import com.mt.reggie.mapper.CategoryMapper;
import com.mt.reggie.service.CategoryService;
import com.mt.reggie.service.DishService;
import com.mt.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //添加查询条件，根据分类id进行查询菜品数据
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId,id);
        long count1 = dishService.count(lqw);
        //如果已经关联，抛出一个业务异常
        if(count1 > 0){
            throw new CustomException("当前分类下关联了菜品，不能删除");//已经关联菜品，抛出一个业务异常
        }

        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(Setmeal::getCategoryId,id);
        long count2 = setmealService.count(lqw1);
        if(count2 > 0){
            throw new CustomException("当前分类下关联了套餐，不能删除");//已经关联套餐，抛出一个业务异常
        }
        //正常删除分类
        super.removeById(id);
    }
}
