package com.mt.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel("员工信息")
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("员工id")
    private Long id;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("员工姓名")
    private String name;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("性别")
    private String sex;
    @ApiModelProperty("编号")
    private String idNumber; //驼峰命名法 ---> 映射的字段名为 id_number
    @ApiModelProperty("员工任职状态")
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
