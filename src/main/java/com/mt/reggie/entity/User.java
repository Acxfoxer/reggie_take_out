package com.mt.reggie.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel("手机用户登录信息")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;

    //姓名
    @ApiModelProperty("姓名")
    private String name;

    //手机号
    @ApiModelProperty("手机号")
    private String phone;

    //性别 0 女 1 男
    @ApiModelProperty("性别")
    private String sex;

    //身份证号
    @ApiModelProperty("身份证号")
    private String idNumber;

    //头像
    @ApiModelProperty("头像")
    private String avatar;

    //状态 0:禁用，1:正常
    @ApiModelProperty("状态")
    private Integer status;
}
