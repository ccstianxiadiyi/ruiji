package com.chen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.dao.SetmealDao;
import com.chen.dto.DishDto;
import com.chen.dto.SetmealDto;
import com.chen.pojo.Dish;
import com.chen.pojo.DishFlavor;
import com.chen.pojo.Setmeal;
import com.chen.pojo.SetmealDish;
import com.chen.service.DishService;
import com.chen.service.SetMealDishService;
import com.chen.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealDao, Setmeal> implements SetmealService {
    @Autowired
    private SetMealDishService setMealDishService;


    @Override
    public boolean saveWithDish(SetmealDto dto) {
        this.save(dto);
        List<SetmealDish> setmealDishes = dto.getSetmealDishes();
        boolean b = setMealDishService.saveBatch(setmealDishes.stream().map((item) -> {
            item.setSetmealId(dto.getId());
            return item;
        }).collect(Collectors.toList()));
        if (b) {
            return true;
        }
        return false;
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal meal = this.getById(id);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, meal.getId());

        List<SetmealDish> list = setMealDishService.list(queryWrapper);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(meal, setmealDto);
        setmealDto.setSetmealDishes(list);
        return setmealDto;

    }

    @Transactional
    @Override
    public boolean editOneData(SetmealDto dto) {
        this.updateById(dto);
        setMealDishService.removeById(dto.getId());
        boolean b = setMealDishService.saveBatch(dto.getSetmealDishes().stream().map((item) -> {
            item.setSetmealId(dto.getId());
            return item;
        }).collect(Collectors.toList()));
        if (b) {
            return true;
        }
        return false;
    }
}
