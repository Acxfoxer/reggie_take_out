package com.mt.reggie.common;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Api("业务异常处理")
public class GlobalExceptionHandler {

    //设置异常处理办法
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class,DuplicateKeyException.class})
    public R<String> handlerException(SQLIntegrityConstraintViolationException sq){
        log.error(sq.getMessage());
        if(sq.getMessage().contains("Duplicate entry")){
            String[] split = sq.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    //删除异常
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException e){
        log.error(e.getMessage());
        return R.error(e.getMessage());
    }

    //自定义异常
    @ExceptionHandler(Exception.class)
    public R<String> handleException(Exception e){
        log.error(e.getMessage());
        return R.error(e.getMessage());
    }
}
