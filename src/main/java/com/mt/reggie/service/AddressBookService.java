package com.mt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mt.reggie.entity.AddressBook;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AddressBookService extends IService<AddressBook> {
}
