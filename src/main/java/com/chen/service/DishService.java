package com.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.dto.DishDto;
import com.chen.pojo.Dish;

public interface DishService extends IService<Dish> {
    boolean saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    boolean editWithFlavor(DishDto dishDto);
}
