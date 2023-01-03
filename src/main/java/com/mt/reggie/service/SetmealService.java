package com.mt.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mt.reggie.dto.SetmealDto;
import com.mt.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public SetmealDto getByIdWithDish(Long id);

    public boolean updateWithDish(SetmealDto setmealDto);

    public boolean deleteWithDish(List<Long> ids);
}
