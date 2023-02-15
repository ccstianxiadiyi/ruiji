package com.chen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.dao.OrderDetailsDao;
import com.chen.pojo.OrderDetail;
import com.chen.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailsDao, OrderDetail> implements OrderDetailService {
}
