package com.mt.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
//自定义元数据对象处理器
public class MyMetaObjectHandler implements MetaObjectHandler {
    //添加时,属性自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        if(metaObject.hasSetter("creatTime")){
            metaObject.setValue("createTime", LocalDateTime.now());
        }if(metaObject.hasSetter("updateTime")){
            metaObject.setValue("updateTime", LocalDateTime.now());
        }if(metaObject.hasSetter("createUser")){
            metaObject.setValue("createUser", BaseContext.getCurrentId());
        }if(metaObject.hasSetter("updateUser")){
            metaObject.setValue("updateUser", BaseContext.getCurrentId());
        }
    }

    //更新操作时,属性自动填充
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        if(metaObject.hasSetter("updateTime")){
            metaObject.setValue("updateTime", LocalDateTime.now());
        }if(metaObject.hasSetter("updateUser")){
            metaObject.setValue("updateUser", BaseContext.getCurrentId());
        }
    }
}
