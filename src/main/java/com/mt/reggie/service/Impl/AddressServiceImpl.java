package com.mt.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mt.reggie.entity.AddressBook;
import com.mt.reggie.mapper.AddressBookMapper;
import com.mt.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
