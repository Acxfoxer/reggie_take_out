package com.mt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mt.reggie.common.R;
import com.mt.reggie.dto.DishDto;
import com.mt.reggie.entity.Category;
import com.mt.reggie.entity.Dish;
import com.mt.reggie.entity.DishFlavor;
import com.mt.reggie.service.CategoryService;
import com.mt.reggie.service.DishFlavorService;
import com.mt.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    private final DishService dishService;
    private final CategoryService categoryService;

    private final DishFlavorService dishFlavorService;

    @Autowired//注解可以省略
    public DishController(DishService dishService, CategoryService categoryService, DishFlavorService dishFlavorService) {
        this.dishService = dishService;
        this.categoryService = categoryService;
        this.dishFlavorService = dishFlavorService;
    }
    private DishDto dto;

    //分页
    @GetMapping("/page")
    public R<IPage<DishDto>> page(int page,int pageSize,String name){
        //创建封装分页数据对象
        IPage<Dish> page1 = new Page<>(page,pageSize);
        //创建复制的分页数据对象
        IPage<DishDto> pageInfo = new Page<>(page,pageSize);
        //设置查询条件
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Dish::getName,name);
        dishService.page(page1,lqw);
        //使用spring的BeanUtils的copyProperties方法,注意因为Records属性有类型,不能直接复制
        BeanUtils.copyProperties(page1,pageInfo,"records");
        //使用stream流完成复制
        List<DishDto> records = page1.getRecords().stream().map(dish -> {
            // 创建DishDto对象
            dto = new DishDto();
            //再次运用BeanUtils方法将records的数据类型Dish转成DishDto
            BeanUtils.copyProperties(dish,dto);
            //复制完成后,dishDto中除了categoryName外其他全部属性不为空,通过dish的分类id查询出分类名字
            Category category = categoryService.getById(dish.getCategoryId());
            //非空判断
            if(category!=null){
                //设置dto中的分类名字
                dto.setCategoryName(category.getName());
            }
            return dto;
        }).collect(Collectors.toList());
        //records完成类型转变后,在添加进pageInfo
        pageInfo.setRecords(records);
        return R.success(pageInfo);
    }

    //增加菜品
    @PostMapping
    public R<String> add(@RequestBody DishDto dto){
        boolean flag = dishService.saveWithFlavor(dto);
        return flag?R.success("添加成功"):R.error("添加失败");
    }

    //根据id查询控制回显
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable("id") long id){
        DishDto dto = dishService.getByIdWithFlavor(id);
        return R.success(dto);
    }

    //套餐管理的添加菜品功能
    @GetMapping("/list")
    public  R<List<DishDto>> listById(Dish dish){
        //创建查询条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加条件,状态为启售的菜品
        lqw.eq(Dish::getStatus,1);
        //添加排序条件
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lqw);
        List<DishDto> dishDtoList = list.stream().map(item->{
            dto = new DishDto();
            BeanUtils.copyProperties(item,dto);
            //创建条件构造器
            LambdaQueryWrapper<DishFlavor> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lqw1);
            if(dishFlavorList!=null){
                //设置dishDto里面的口味集合
                dto.setFlavors(dishFlavorList);
            }
            return dto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }

    //修改菜品
    @PutMapping
    public R<String> update(@RequestBody DishDto dto){
        boolean flag = dishService.updateWithFlavor(dto);
        return flag?R.success("编辑成功"):R.error("编辑失败");
    }

    //删除菜品,批量删除菜品
    @DeleteMapping
    public R<String> delete(Long[] ids){
        dishService.deleteByIdsWithFlavor(ids);
        return R.success("删除成功");
    }

    //批量更改菜品状态
    @PostMapping("/status/{status}")
    public R<String> updateByIdS(@PathVariable("status")Integer status,Long[]ids){
        //创建更新条件构造器
        LambdaUpdateWrapper<Dish> lqw = new LambdaUpdateWrapper<>();
        lqw.in(Dish::getId,ids).set(Dish::getStatus,status);
        boolean flag = dishService.update(lqw);
        return flag?R.success("编辑成功"):R.error("编辑失败");
    }
}