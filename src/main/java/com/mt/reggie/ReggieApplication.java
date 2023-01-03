package com.mt.reggie;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j//是lombok中提供的注解, 用来通过slf4j记录日志,可以引入log
@SpringBootApplication
@MapperScan("com.mt.reggie.mapper")//添加mapper扫描
@ServletComponentScan//添加Servlet扫描的注解,可以扫描到自定义的filter类
@EnableTransactionManagement//开启事务管理
public class  ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目成功启动了......");
    }


}
