package com.example.printserver.service;

import com.example.printserver.pojo.SearchOrder;
import com.example.printserver.pojo.dao.Order;
import com.example.printserver.pojo.dao.OrderView;
import com.example.printserver.result.CommonResult;

import java.util.ArrayList;

public interface OrderService {
    public ArrayList<OrderView> getOrders(Integer id, Integer type);

    public CommonResult addOrder(Order order);
    public Long[] getCustomerOrderStatistics(Integer id,Integer type);
    public CommonResult getOrder(String id);
    public CommonResult getOrders(SearchOrder searchOrder,Integer type);
    public CommonResult deleteOrder(Order order);

    public String finishOrder(Order oid);
}
