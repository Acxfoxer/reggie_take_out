package com.mt.reggie.controller;

import com.mt.reggie.service.OrderDetailService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orderDetail")
@Api(tags = "订单支付等相关接口")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;
}
