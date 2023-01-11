package com.mt.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mt.reggie.common.BaseContext;
import com.mt.reggie.common.R;
import com.mt.reggie.entity.AddressBook;
import com.mt.reggie.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
@Api(tags = "地址相关接口")
public class AddressBookController {
    /**
     * controller主要开发的功能:
     * 1.新增地址逻辑说明： 需要记录当前是哪个用户的地址(关联当前登录用户)
     * 2.设置默认地址:
     * 每个用户可以有很多地址，但是默认地址只能有一个 ；
     * 先将该用户所有地址的is_default更新为0 , 然后将当前的设置的默认地址的is_default设置为1
     * 3.根据id查询地址
     * 4.查询默认地址
     * 根据当前登录用户ID 以及 is_default进行查询，查询当前登录用户is_default为1的地址信息
     * 5.查询指定用户的全部地址
     * 根据当前登录id,查询所有的地址列表
     * */
    @Autowired
    private AddressBookService addressBookService;

    //新增地址
    @ApiOperation("新增地址")
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    //设置默认地址
    @ApiOperation("设置默认地址")
    @PutMapping("/default")
    public R<AddressBook> updateDefault(@RequestBody AddressBook addressBook){
        //创建更新条件构造器
        LambdaUpdateWrapper<AddressBook> lqw = new LambdaUpdateWrapper<>();
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        lqw.set(AddressBook::getIsDefault,0);
        addressBookService.update(lqw);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    //查询登录用户全部的地址
    @ApiOperation("设置所有地址")
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        //设置地址表的用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        //输出到控制台
        log.info("addressBook:{}",addressBook);
        //创建查询条件构造器
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());
        //设置可选的条件
        lqw.like(addressBook.getConsignee()!=null,AddressBook::getConsignee,addressBook.getConsignee());
        lqw.like(addressBook.getPhone()!=null,AddressBook::getPhone,addressBook.getPhone());
        //设置排序条件,按照更新时间降序
        lqw.orderByDesc(AddressBook::getUpdateTime);
        return R.success(addressBookService.list(lqw));
    }

    //根据id查询地址
    @ApiOperation("根据id查询地址地址")
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable("id")Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return addressBook!=null?R.success(addressBook):R.error("该地址不存在");
    }

    //查询默认地址
    @ApiOperation("查询默认地址")
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        //创建条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        System.out.println("默认地址为"+addressBook.getConsignee());
        return R.success(addressBook);
    }

    //修改地址信息
    @ApiOperation("修改默认地址")
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        boolean flag = addressBookService.updateById(addressBook);
        return flag?R.success("编辑成功"):R.error("编辑失败");
    }
}
