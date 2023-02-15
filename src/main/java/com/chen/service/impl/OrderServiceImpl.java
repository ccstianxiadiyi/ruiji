package com.chen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chen.dao.OrderDao;
import com.chen.pojo.Orders;
import com.chen.service.OrderSercice;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderDao, Orders> implements OrderSercice {
}
