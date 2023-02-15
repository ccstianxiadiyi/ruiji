package com.chen.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chen.common.R;
import com.chen.pojo.Category;
import com.chen.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /*
     * 分页查询
     * */
    @GetMapping("/page")
    public R<Page> getPage(@RequestParam Integer page, @RequestParam Integer pageSize) {
        Page page1 = new Page(page, pageSize);
        categoryService.page(page1);
        return R.success(page1);
    }

    /*
     * 编辑分类
     * */
    @PutMapping
    public R<String> add(@RequestBody Category category, HttpServletRequest request) {
//        category.setUpdateTime(LocalDateTime.now());
//        category.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        boolean b = categoryService.updateById(category);
        if (b) {
            return R.success("添加成功");
        } else {
            return R.error("添加失败");
        }
    }

    /*增加分类*/
    @PostMapping
    public R<String> addCategory(@RequestBody Category category) {
        boolean save = categoryService.save(category);
        if (save) {
            return R.success("添加成功");
        }
        return R.error("添加失败");
    }

    /*删除分类*/
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long id) {
        boolean b = categoryService.remove(id);
        if (b) {
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }
    /**/


    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }


}
