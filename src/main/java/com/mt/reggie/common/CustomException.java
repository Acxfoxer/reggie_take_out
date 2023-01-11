package com.mt.reggie.common;

import io.swagger.annotations.Api;


public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
