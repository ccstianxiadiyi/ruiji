package com.chen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.common.R;
import com.chen.dto.DishDto;
import com.chen.dto.SetmealDto;
import com.chen.pojo.*;
import com.chen.service.*;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetMealDishService setMealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> getByPage(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize, @RequestParam(value = "name", required = false) String name) {
        Page<Dish> page1 = new Page<>(page, pageSize);
        Page<DishDto> page2 = new Page<>();
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(page1, lambdaQueryWrapper);
        BeanUtils.copyProperties(page1, page2, "records");
        List<Dish> records = page1.getRecords();
        List<DishDto> collect = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if (byId != null) {
                String name1 = byId.getName();
                dishDto.setCategoryName(name1);
            }
            return dishDto;
        }).collect(Collectors.toList());
        page2.setRecords(collect);
        return R.success(page2);
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        boolean b = dishService.saveWithFlavor(dishDto);
        if (b) {
            return R.success("添加成功！");
        }
        return R.error("添加失败");
    }

//    @DeleteMapping
//    public R<String> deleteById(@RequestParam("ids") Long ids) {
//        boolean b = dishService.removeById(ids);
//        if (b) {
//            return R.success("删除成功！");
//
//        }
//        return R.error("删除失败！");
//    }

    @PostMapping("/status/{status}")
    public R<String> editStatus(@PathVariable Integer status, @RequestParam("ids") List<Long> ids) {
        List<Dish> collect = ids.stream().map((item) -> {
            Dish dish = new Dish();
            dish.setStatus(status);
            dish.setId(Long.parseLong(item.toString()));
            return dish;
        }).collect(Collectors.toList());
        boolean b = dishService.updateBatchById(collect);
        /*
         * 这一块如果菜品的status为0的话，含有此菜品的套餐对应也要停售 功能还没有完全实现 待后续补充
         * */
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getDishId, ids);
        List<SetmealDish> list = setMealDishService.list(queryWrapper);
        List<Setmeal> collect1 = list.stream().map((item) -> {
            Long setmealId = item.getSetmealId();
            Setmeal setmeal = new Setmeal();
            setmeal.setId(setmealId);
            if (status == 0) {
                setmeal.setStatus(0);
            }
            return setmeal;
        }).collect(Collectors.toList());
        setmealService.updateBatchById(collect1);
        return R.success("修改成功");
    }

    /*
     * 这一块如果菜品删除了，那么对应的套餐里面的菜品也要删除
     * */
    @DeleteMapping
    public R<String> deleteByIds(@RequestParam("ids") List ids) {

        boolean b = dishService.removeByIds(ids);
        ids.stream().map((item) -> {
            LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SetmealDish::getDishId, ids);
            setMealDishService.remove(queryWrapper);
            return item;

        }).collect(Collectors.toList());
        if (b) {
            return R.success("删除成功！");

        }
        return R.error("删除失败！");
    }

    @GetMapping("/{id}")
    public R<DishDto> getOne(@PathVariable Long id) {
        DishDto byIdWithFlavor = dishService.getByIdWithFlavor(id);
        return R.success(byIdWithFlavor);

    }

    @PutMapping
    public R<String> editData(@RequestBody DishDto dishDto) {
        boolean b = dishService.editWithFlavor(dishDto);
        if (b) {
            return R.success("成功");
        }
        return R.error("失败");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }


}
