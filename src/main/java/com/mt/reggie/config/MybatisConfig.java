package com.mt.reggie.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {

    //分页插件
    @Bean
    public MybatisPlusInterceptor getInterceptor(){
        //创建拦截器对象
        MybatisPlusInterceptor mp = new MybatisPlusInterceptor();
        //添加分页插件到拦截器对象
        mp.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mp;
    }

    //乐观锁
    @Bean
    public MybatisPlusInterceptor getOptimistInterceptor(){
        MybatisPlusInterceptor mp = new MybatisPlusInterceptor();
        mp.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mp;
    }
}
