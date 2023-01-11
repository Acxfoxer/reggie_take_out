package com.mt.reggie.dto;

import com.mt.reggie.entity.Dish;
import com.mt.reggie.entity.DishFlavor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    @ApiModelProperty("口味")
    private List<DishFlavor> flavors = new ArrayList<>();
    @ApiModelProperty("分类名称")
    private String categoryName;
    @ApiModelProperty("copies")
    private Integer copies;
}
