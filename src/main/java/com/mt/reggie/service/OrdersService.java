package com.mt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mt.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders);
}
