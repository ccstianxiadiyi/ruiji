package com.chen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.common.CustomException;
import com.chen.dao.CategoryDao;
import com.chen.pojo.Category;
import com.chen.pojo.Dish;
import com.chen.pojo.Setmeal;
import com.chen.service.CategoryService;
import com.chen.service.DishService;
import com.chen.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public boolean remove(Long id) {
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(lambdaQueryWrapper);
        if (count > 0) {
            throw new CustomException("当前分类下关联了菜品不能删除");
        }
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = setmealService.count(queryWrapper);
        if (count1 > 0) {
            throw new CustomException("当前分类下关联了菜品不能删除");
        }
        return true;


    }

}
