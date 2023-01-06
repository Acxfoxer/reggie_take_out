package com.mt.reggie;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mt.reggie.entity.Dish;
import com.mt.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@Slf4j
public class ReggieApplicationTests {
    @Autowired
    private  DishService dishService;

    @Test
    void testUpdate(){
        //创建更新条件构造器
        LambdaUpdateWrapper<Dish> lqw = new LambdaUpdateWrapper<>();
        lqw.in(Dish::getId,1397849739276890114L).set(Dish::getStatus,0);
        dishService.update(lqw);
    }

    @Test
    void testToHexString(){
        int i = 170;
        System.out.println("i+1为:"+i+1);
        String a=Integer.toHexString(i);
        System.out.println(a);
    }

    //测试图片文件名
    @Test
    void testUUIDFileName(){
        UUID uuid = UUID.randomUUID();
        String name = uuid+"张三";
        System.out.println("名字为:{}"+name);
        Map<String,String> map = new HashMap<>();
    }
}
