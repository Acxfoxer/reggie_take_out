package com.mt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mt.reggie.common.R;
import com.mt.reggie.entity.Orders;
import com.mt.reggie.entity.QueryOrder;
import com.mt.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    private  OrdersService ordersService;
    @Autowired
    public void setOrderController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    /**
     * 用户下单功能
     * @param orders 用户订单表
     * @return 返回成功数据
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    //商家后台查看订单信息
    @GetMapping("/page")
    public R<IPage<Orders>> page(QueryOrder queryOrder){
        //创建分页查询后的分页数据对象
        IPage<Orders> page = new Page<>(queryOrder.getPage(),queryOrder.getPageSize());
        //创建条件构造器
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        //设置根据订单号模糊查询条件
        lqw.like(queryOrder.getId()!=null,Orders::getId,queryOrder.getId());
        //设置时间查询条件
        lqw.in(queryOrder.getBeginTime()!=null,Orders::getOrderTime,queryOrder.getBeginTime(),queryOrder.getEndTime());
        //设置排序条件-根据下单时间升序;
        lqw.orderByAsc(Orders::getOrderTime);
        ordersService.page(page,lqw);
        return R.success(page);
    }

    //移动端用户界面分页查询
    @GetMapping("/userPage")
    public R<IPage<Orders>> userPage(QueryOrder queryOrder){
        //创建封装分页查询数据的对象
        IPage<Orders> page = new Page<>(queryOrder.getPage(),queryOrder.getPageSize());
        ordersService.page(page);
        return R.success(page);
    }

}
