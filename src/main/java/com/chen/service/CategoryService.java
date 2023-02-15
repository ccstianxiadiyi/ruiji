package com.chen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chen.pojo.Category;
import org.springframework.stereotype.Service;


public interface CategoryService extends IService<Category> {

    boolean remove(Long id);
}
