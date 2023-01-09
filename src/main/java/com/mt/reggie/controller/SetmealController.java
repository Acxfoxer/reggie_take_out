package com.mt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mt.reggie.common.R;
import com.mt.reggie.dto.SetmealDto;
import com.mt.reggie.entity.Category;
import com.mt.reggie.entity.Dish;
import com.mt.reggie.entity.Setmeal;
import com.mt.reggie.service.CategoryService;
import com.mt.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    //分页
    @Cacheable(value = "setmealCache",key = "#page+'_'+#pageSize+'_'+#name")
    @GetMapping("/page")
    public R<IPage<SetmealDto>> page(int page,int pageSize,String name){
        //创建封装分页数据的对象
        IPage<Setmeal> pageInfo = new Page<>(page,pageSize);
        IPage<SetmealDto> pageInfo1 = new Page<>();
        //创建条件构造器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(name!=null,Setmeal::getName,name);
        lqw.orderByDesc(Setmeal::getUpdateTime);
        //调用查询方法
        setmealService.page(pageInfo,lqw);
        //复制IPage对象
        BeanUtils.copyProperties(pageInfo,pageInfo1,"records");
        //使用Stream流完成records类型转换
        List<SetmealDto> records = pageInfo.getRecords().stream().map(item->{
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //获取分类id
            Long categoryId = setmealDto.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        pageInfo1.setRecords(records);
        return R.success(pageInfo1);
    }

    //新增套餐
    @CacheEvict(value = "setmealCache",allEntries = true)
    @PostMapping
    public R<String> add(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("新增成功");
    }

    //根据id查询控制修改窗口回显
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable("id")Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return setmealDto!=null?R.success(setmealDto):R.error("修改失败");
    }

    //编辑当前套餐信息功能
    @PutMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> update(@RequestBody SetmealDto setmealDto){
        boolean flag = setmealService.updateWithDish(setmealDto);
        return flag?R.success("修改成功"):R.error("修改失败");
    }

    //批量删除,删除功能
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(@RequestParam("ids") List<Long> ids){
        boolean flag = setmealService.deleteWithDish(ids);
        return flag?R.success("删除成功"):R.error("删除失败");
    }


    //批量更改菜品状态
    @PostMapping("/status/{status}")
    public R<String> updateByIdS(@PathVariable("status")Integer status,Long[]ids){
        //创建更新条件构造器
        LambdaUpdateWrapper<Setmeal> lqw = new LambdaUpdateWrapper<>();
        lqw.in(Setmeal::getId,ids).set(Setmeal::getStatus,status);
        boolean flag = setmealService.update(lqw);
        return flag?R.success("编辑成功"):R.error("编辑失败");
    }


    /**
     *
     * @param setmeal 接受前端参数
     */
    //套餐展示
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.name")
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        //创建条件构造器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lqw.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        lqw.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lqw);
        return list!=null?R.success(list):R.error("套餐不存在,请检查");
    }
}
