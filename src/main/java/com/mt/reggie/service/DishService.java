package com.mt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mt.reggie.dto.DishDto;
import com.mt.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public boolean saveWithFlavor(DishDto dishDto);
    //根据id查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(long id);

    //修改菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public boolean updateWithFlavor(DishDto dishDto);

    //自定义删除方法
    public boolean deleteByIdsWithFlavor(Long[] ids);
}
