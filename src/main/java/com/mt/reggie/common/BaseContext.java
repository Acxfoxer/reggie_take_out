package com.mt.reggie.common;

import io.swagger.annotations.Api;

@Api("线程获取用户id")
public class BaseContext {
    //创建线程局部变量对象
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();


    //设置当前线程的线程局部变量值
    public static void setCurrentId(long id){
        threadLocal.set(id);
    }

    //获取设置的值的方法
    public static long getCurrentId(){
        //返回当前线程所对应的线程局部变量的值
        return threadLocal.get();
    }
}
