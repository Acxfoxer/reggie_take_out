package com.mt.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mt.reggie.dto.DishDto;
import com.mt.reggie.entity.Dish;
import com.mt.reggie.entity.DishFlavor;
import com.mt.reggie.mapper.DishMapper;
import com.mt.reggie.service.DishFlavorService;
import com.mt.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{
    private  DishFlavorService dishFlavorService;


    //基于Setter依赖注入
    @Autowired
    public void setDishFlavorService(DishFlavorService dishFlavorService) {
        this.dishFlavorService = dishFlavorService;
    }

    @Override
    @Transactional
    public boolean saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品id
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors().stream().map((dishFlavor)->{
            dishFlavor.setDishId(dishId);
            return dishFlavor;
        }).collect(Collectors.toList());


        //保存菜品口味数据到菜品口味表dish_flavor
        return dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(long id) {
        Dish dish = this.getById(id);
        DishDto dto = new DishDto();
        BeanUtils.copyProperties(dish,dto);
        //创建条件创造器
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(lqw);
        dto.setFlavors(flavors);
        return dto;
    }


    @Override
    @Transactional
    public boolean updateWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper();
        lqw.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(lqw);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().peek(item ->
                item.setDishId(dishDto.getId())).collect(Collectors.toList());

        return dishFlavorService.saveBatch(flavors);
    }

    //自定义的删除方法
    @Override
    @Transactional
    public boolean deleteByIdsWithFlavor(Long[] ids){
        //创建条件构造器
        LambdaQueryWrapper<DishFlavor> lqw  =new LambdaQueryWrapper<>();
        lqw.in(DishFlavor::getDishId, ids);
        //根据id批量删除DishFlavor的数据
        dishFlavorService.remove(lqw);
        //删除Dish表中的数据
        return this.removeByIds(Arrays.asList(ids));
    }

}
