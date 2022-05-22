package com.example.printserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.printserver.mapper.OrderMapper;
import com.example.printserver.mapper.OrderViewMapper;
import com.example.printserver.pojo.Price;
import com.example.printserver.pojo.SearchOrder;
import com.example.printserver.pojo.dao.Order;
import com.example.printserver.pojo.dao.OrderView;
import com.example.printserver.result.CommonResult;
import com.example.printserver.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service("orderService")
public class OrderServiceImpl implements OrderService {
    OrderMapper orderMapper;

    @Autowired
    public void setBillMapper(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Autowired
    Price price;
    OrderViewMapper orderViewMapper;

    @Autowired
    public void setBillViewMapper(OrderViewMapper orderViewMapper) {
        this.orderViewMapper = orderViewMapper;
    }

    public ArrayList<OrderView> getOrders(Integer id, Integer type) {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Future future = pool.submit((Callable<Object>) () -> {
            QueryWrapper<OrderView> wrapper = new QueryWrapper();
            if (type == 1) {
                wrapper.eq("customer_id", id).orderByDesc("order_time");
            } else {
                wrapper.eq("shop_id", id).orderByDesc("order_time");
            }
            List<OrderView> orderViews = orderViewMapper.selectList(wrapper);
            return orderViews;
        });
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                try {
                    return (ArrayList<OrderView>) future.get();
                } catch (Exception e) {
                    return null;
                }
            }
        }
    }

    @Override
    public Long[] getCustomerOrderStatistics(Integer id, Integer type) {
        Long[] longs;
        QueryWrapper<Order> wrapper = new QueryWrapper<Order>();
        Long count ;
        Long color;
        Long duplex;
        Long unPay;
        if (type == 1) {
            wrapper.eq("customer_id", id);
            wrapper.ne("order_state", -1);
            count = orderMapper.selectCount(wrapper);
            wrapper.eq("customer_id", id);
            wrapper.eq("color", 2);
            wrapper.ne("order_state", -1);
            color = orderMapper.selectCount(wrapper);
            wrapper = new QueryWrapper<Order>();
            wrapper.ne("order_state", -1);
            wrapper.eq("customer_id", id);
            wrapper.in("duplex", Arrays.asList(2, 3));
            duplex = orderMapper.selectCount(wrapper);
            wrapper = new QueryWrapper<Order>();
            wrapper.ne("order_state", -1);
            wrapper.eq("customer_id", id);
            wrapper.eq("pay_state", 0);
            unPay = orderMapper.selectCount(wrapper);
        } else if (type == 2) {
            wrapper.eq("shop_id", id);
            count = orderMapper.selectCount(wrapper);
            wrapper.eq("shop_id", id);
            wrapper.eq("color", 2);
            color = orderMapper.selectCount(wrapper);
            wrapper = new QueryWrapper<Order>();
            wrapper.eq("shop_id", id);
            wrapper.in("duplex", Arrays.asList(2, 3));
            duplex = orderMapper.selectCount(wrapper);
            wrapper = new QueryWrapper<Order>();
            wrapper.eq("shop_id", id);
            wrapper.eq("pay_state", 0);
            unPay = orderMapper.selectCount(wrapper);
        } else {
            count = orderMapper.selectCount(wrapper);
            wrapper.eq("color", 2);
            color = orderMapper.selectCount(wrapper);
            wrapper = new QueryWrapper<Order>();
            wrapper.in("duplex", Arrays.asList(2, 3));
            duplex = orderMapper.selectCount(wrapper);
            wrapper = new QueryWrapper<Order>();
            wrapper.eq("pay_state", 0);
            unPay = orderMapper.selectCount(wrapper);

        }
        longs = new Long[]{count, color, duplex, unPay};
        return longs;
    }

    @Override
    public CommonResult getOrder(String id) {
        return CommonResult.success(orderViewMapper.selectById(Integer.valueOf(id)));
    }

    @Override
    public CommonResult getOrders(SearchOrder searchOrder, Integer type) {
        QueryWrapper<OrderView> wrapper = new QueryWrapper<OrderView>();
        if (type == 1) {
            wrapper.eq("customer_id", searchOrder.getId());
        } else if (type == 2) {
            wrapper.eq("shop_id", searchOrder.getId());
        }
        wrapper.like("filename", searchOrder.getNamePart());
        wrapper.in("color", Arrays.asList(searchOrder.getColor()));
        wrapper.in("duplex", Arrays.asList(searchOrder.getDuplex()));
        wrapper.in("pay_state", Arrays.asList(searchOrder.getPayState()));
        wrapper.in("order_state", Arrays.asList(searchOrder.getOrderState()));
        wrapper.orderByDesc("order_time");
        return CommonResult.success(orderViewMapper.selectList(wrapper));
    }

    @Override
    public CommonResult deleteOrder(Order order) {
        order.setOrderState(-1);
        return CommonResult.success(orderMapper.updateById(order), "删除成功");
    }

    @Override
    public CommonResult addOrder(Order order) {
        double orderPrice = order.getOrderPage() * price.getDuplex()[order.getDuplex() - 1] * price.getColor()[order.getColor() - 1] * order.getPrintNum();
        BigDecimal bigDecimal = new BigDecimal(orderPrice);
        double price = bigDecimal.setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        order.setPrice(price);
        orderMapper.insert(order);
        OrderView orderView = orderViewMapper.selectById(order.getOid());
        return CommonResult.success(orderView);
    }

}
