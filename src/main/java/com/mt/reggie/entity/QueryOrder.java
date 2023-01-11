package com.mt.reggie.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

//封装订单查询接受的前端数据javabean类
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("订单查询JAVABean类")
public class QueryOrder implements Serializable {
    private static final Long SerialVersionUID = 1L;
    private Integer page; //当前页码
    private Integer pageSize;//每页显示记录数
    private Long id;//订单号
    private Date beginTime;//下单开始时间
    private Date endTime;//查询截至时间
}
