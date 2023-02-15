package com.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.dto.DishDto;
import com.chen.dto.SetmealDto;
import com.chen.pojo.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    boolean saveWithDish(SetmealDto dto);

    public SetmealDto getByIdWithDish(Long id);

    boolean editOneData(SetmealDto dto);
}
