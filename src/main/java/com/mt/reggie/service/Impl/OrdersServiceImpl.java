package com.mt.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mt.reggie.common.BaseContext;
import com.mt.reggie.common.CustomException;
import com.mt.reggie.entity.*;
import com.mt.reggie.mapper.OrdersMapper;
import com.mt.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 具体实现步骤如下
     * A. 获得当前用户id, 查询当前用户的购物车数据
     * <p>
     * B. 根据当前登录用户id, 查询用户数据
     * <p>
     * C. 根据地址ID, 查询地址数据
     * <p>
     * D. 组装订单明细数据, 批量保存订单明细
     * <p>
     * E. 组装订单数据, 批量保存订单数据
     * <p>
     * F. 删除当前用户的购物车列表数据
     *
     * @param orders
     */
    @Override
    public void submit(Orders orders) {
        //获得当前用户id
        long userId = BaseContext.getCurrentId();
        /*查询当前用户的购物车数据
         * 1.创建条件构造器
         * */
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        //2.条件为userId
        lqw.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lqw);
        //判断是否能查出购物车数据,查不出来提示
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空，不能下单");
        }
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("地址有误,无法下单,请重新再试");
        }
        //使用mb的订单号生成工具生成订单号
        long orderId = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);
        //组织订单明细信息
        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCartList, orderDetail);
            //总金额=单价*数量
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        //查询用户数据
        User user = userService.getById(userId);
        //组装订单数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(lqw);

    }
}
