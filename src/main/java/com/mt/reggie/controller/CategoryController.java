package com.mt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mt.reggie.common.R;
import com.mt.reggie.entity.Category;
import com.mt.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    private final RedisTemplate<Object,Object> redisTemplate;

    public CategoryController(CategoryService categoryService, RedisTemplate redisTemplate) {
        this.categoryService = categoryService;
        this.redisTemplate = redisTemplate;
    }

    //新增菜品分类
    @PostMapping
    public R<String> add(@RequestBody Category category){
        boolean flag = categoryService.save(category);
        return flag?R.success("添加成功"):R.error("添加失败");
    }

    //分页操作
    @GetMapping("/page")
    public R<IPage<Category>> page(int page,int pageSize){
        //创建封装分页数据对象
        IPage<Category> pageInfo = new Page<>(page,pageSize);
        //设置排序条件
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //调用分页方法
        categoryService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    //修改分类
    @PutMapping
    public R<String> update(@RequestBody Category category){
        boolean flag = categoryService.updateById(category);
        return flag?R.success("编辑分类成功"):R.error("编辑分类失败");
    }

    //删除分类
    @DeleteMapping
    public R<String> delete(Long id){
        categoryService.remove(id);
        return R.success("删除分类成功");
    }
    //根据id查询分类数据
    @GetMapping("/{id}")
    public R<Category> getById(@PathVariable("id") Long id){
        //输出日志
        log.info("查询的id为{}",id);
        Category category = categoryService.getById(id);
        return category!=null?R.success(category):R.error("请刷新后再试");
    }

    //查询分类数据
    @Cacheable(value = "category",key = "#p0.type")
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //创建条件构造器对象
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        //设置查询条件
        lqw.eq(category.getType()!=null,Category::getType,category.getType());
        lqw.like(category.getName()!=null,Category::getName,category.getName());
        //设置排序条件
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lqw);
        return  R.success(list);
    }
}
