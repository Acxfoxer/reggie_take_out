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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
@Api(tags="菜品相关接口")
public class DishController {
    private final DishService dishService;
    private final CategoryService categoryService;

    private final DishFlavorService dishFlavorService;

    @Autowired//注解可以省略
    public DishController(DishService dishService, CategoryService categoryService, DishFlavorService dishFlavorService, RedisTemplate<Object, Object> redisTemplate) {
        this.dishService = dishService;
        this.categoryService = categoryService;
        this.dishFlavorService = dishFlavorService;
        this.redisTemplate = redisTemplate;
    }

    private final RedisTemplate<Object,Object> redisTemplate;
    private DishDto dto;

    //分页
    @ApiOperation("分页")
    @GetMapping("/page")
    @Cacheable(value = "dishCache",key = "#page+'_'+#pageSize+'_'+#name")
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
    @ApiOperation("增加功能")
    @PostMapping
    @CacheEvict(value = "dishCache",key ="'dish_' + #dto.getCategoryId()+'_1'" )
    public R<String> add(@RequestBody DishDto dto){
        boolean flag = dishService.saveWithFlavor(dto);
        //删除所有菜品缓存数据
        Set<Object> keys = redisTemplate.keys("dish*");
        if(keys!=null){
            redisTemplate.delete(keys);
        }
        return flag?R.success("添加成功"):R.error("添加失败");
    }

    //根据id查询控制回显
    @ApiOperation("回显功能")
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable("id") long id){
        DishDto dto = dishService.getByIdWithFlavor(id);
        return R.success(dto);
    }

    //查询菜品
    @ApiOperation("根据分类查询菜品")
    @Cacheable(value = "dishCache",key ="'dish_' + #dish.getCategoryId() +'_'+ #dish.getStatus()" )
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
        //redis不存在,查询完毕后将数据存入redis
        //redisTemplate.opsForValue().set(key,dishDtoList,30, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }

    //修改菜品
    @ApiOperation("修改菜品")
    @PutMapping
    public R<String> update(@RequestBody DishDto dto){
        boolean flag = dishService.updateWithFlavor(dto);
        //删除所有菜品缓存数据.
        Set<Object> keys = redisTemplate.keys("dish_*");
        if(keys!=null){
            redisTemplate.delete(keys);
        }
        return flag?R.success("编辑成功"):R.error("编辑失败");
    }

    //删除菜品,批量删除菜品
    @ApiOperation("批量删除")
    @DeleteMapping
    public R<String> delete(Long[] ids){
        dishService.deleteByIdsWithFlavor(ids);
        //删除所有菜品缓存数据
        Set<Object> keys = redisTemplate.keys("dish_*");
        if(keys!=null){
            redisTemplate.delete(keys);
        }
        return R.success("删除成功");
    }

    //批量更改菜品状态
    @ApiOperation("批量更改菜品售卖状态")
    @PostMapping("/status/{status}")
    @CacheEvict(value = "dishCache",allEntries = true)
    public R<String> updateByIdS(@PathVariable("status")Integer status,Long[]ids){
        //创建更新条件构造器
        LambdaUpdateWrapper<Dish> lqw = new LambdaUpdateWrapper<>();
        lqw.in(Dish::getId, (Object) ids).set(Dish::getStatus,status);
        boolean flag = dishService.update(lqw);
        return flag?R.success("编辑成功"):R.error("编辑失败");
    }
}
