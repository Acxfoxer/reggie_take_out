package com.mt.reggie.dto;

import com.mt.reggie.entity.Setmeal;
import com.mt.reggie.entity.SetmealDish;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    @ApiModelProperty("套餐关联列表")
    private List<SetmealDish> setmealDishes; //套餐关联菜品列表
    @ApiModelProperty("套餐分类名称")
    private String categoryName;//套餐分类名称
}
