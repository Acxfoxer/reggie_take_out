package com.mt.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber; //驼峰命名法 ---> 映射的字段名为 id_number

    private Integer status;
    //对添加了该注解的字段自动填充
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //进行插入和更新时进行插入和更新时自动填充。
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    //对添加了该注解的字段自动填充
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    //进行插入和更新时进行插入和更新时自动填充。
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
