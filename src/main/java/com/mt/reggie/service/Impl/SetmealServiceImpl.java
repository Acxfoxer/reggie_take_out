package com.mt.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mt.reggie.common.CustomException;
import com.mt.reggie.dto.SetmealDto;
import com.mt.reggie.entity.Setmeal;
import com.mt.reggie.entity.SetmealDish;
import com.mt.reggie.mapper.SetmealMapper;
import com.mt.reggie.service.SetmealDishService;
import com.mt.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService service;
    //自定义新增方法
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存新增的基本信息到Setmeal表中
        this.save(setmealDto);
        Long id = setmealDto.getId();//获取套餐id
        //添加套餐关联的菜品数据到SetmealDish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map(setmealDish->{
            setmealDish.setSetmealId(id);
            return setmealDish;
        }).collect(Collectors.toList());
        service.saveBatch(setmealDishes);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal s = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        //拷贝对象
        BeanUtils.copyProperties(s,setmealDto);
        //创建条件构造器
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,s.getId());
        List<SetmealDish> list = service.list(lqw);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }


    //自定义修改方法
    @Override
    @Transactional
    public boolean updateWithDish(SetmealDto setmealDto) {
        //更新Setmeal表的基本信息
        this.updateById(setmealDto);
        //清理当前SetmealDish的数据
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmealDto!=null,SetmealDish::getSetmealId,setmealDto.getId());
        //调用remove方法,删除原来的信息
        service.remove(lqw);
        //重新添加
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());
        return service.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public boolean deleteWithDish(List<Long> ids) {
        //强行删除
        /*LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.in(SetmealDish::getSetmealId,ids);
        service.remove(lqw);
        return this.removeByIds(ids);*/
        //根据套餐是否启售来判断是否能删除
        LambdaQueryWrapper<Setmeal> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(Setmeal::getStatus,1);
        lqw1.in(Setmeal::getId,ids);
        long count = this.count(lqw1);
        if(count>0){
            throw new CustomException("当前套餐处于启售状态,无法删除");//已经关联菜品，抛出一个业务异常
        }
        this.removeByIds(ids);


        return super.removeByIds(ids);
    }
}
