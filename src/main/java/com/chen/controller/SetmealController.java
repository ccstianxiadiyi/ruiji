package com.chen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.common.CustomException;
import com.chen.common.R;
import com.chen.dao.SetmealDao;
import com.chen.dto.SetmealDto;
import com.chen.pojo.Category;
import com.chen.pojo.Dish;
import com.chen.pojo.Setmeal;
import com.chen.pojo.SetmealDish;
import com.chen.service.CategoryService;
import com.chen.service.SetMealDishService;
import com.chen.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetMealDishService setMealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> page(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize, @RequestParam(value = "name", required = false) String name) {
        Page<Setmeal> page1 = new Page<>(page, pageSize);
        Page<SetmealDto> page2 = new Page<SetmealDto>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(page1, queryWrapper);
        BeanUtils.copyProperties(page1, page2, "records");
        List<Setmeal> records = page1.getRecords();
        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if (byId != null) {
                setmealDto.setCategoryName(byId.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        page2.setRecords(collect);
        return R.success(page2);
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status, @RequestParam("ids") List<Long> ids) {
        List<Setmeal> collect = ids.stream().map((item) -> {
                    Setmeal meal = new Setmeal();
                    meal.setId(item);
                    meal.setStatus(status);
                    return meal;
                }
        ).collect(Collectors.toList());
        boolean b = setmealService.updateBatchById(collect);

        if (b) {
            return R.success("更改状态成功");
        }
        return R.error("更改失败");
    }

    @DeleteMapping
    public R<String> deleteDatas(@RequestParam("ids") List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = setmealService.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("改套餐关联了正在出售的菜品，无法删除");
        }
        boolean b = setmealService.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, ids);
        setMealDishService.remove(queryWrapper1);
        return R.success("删除成功");
    }

    @PostMapping
    public R<String> saveMeal(@RequestBody SetmealDto setmealDto) {
        boolean b = setmealService.saveWithDish(setmealDto);
        if (b) {
            return R.success("添加成功");
        }
        return R.error("添加失败");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getOnlyOneData(@PathVariable Long id) {
        SetmealDto dto = setmealService.getByIdWithDish(id);
        return R.success(dto);
    }

    @PutMapping
    public R<String> updateOneData(@RequestBody SetmealDto dto) {
        boolean b = setmealService.editOneData(dto);
        if (b) {
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

    @GetMapping("/list")
    @Cacheable(value = "setMealCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> getDataList(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus, setmeal.getStatus());
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
