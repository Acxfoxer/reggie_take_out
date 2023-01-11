package com.mt.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("分类")
public class Category implements Serializable {
    private static final long serialVersionUID=2L;
    private Long id;
    private Integer type;
    private String name;
    private Integer sort;
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
