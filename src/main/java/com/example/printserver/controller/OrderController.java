package com.example.printserver.controller;

import com.example.printserver.pojo.SearchOrder;
import com.example.printserver.pojo.dao.Order;
import com.example.printserver.pojo.dao.OrderView;
import com.example.printserver.result.CommonResult;
import com.example.printserver.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/order")
public class OrderController {

    OrderService orderService;

    @Autowired
    public void setBillService(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/addOrder")
    public CommonResult addOrder(@RequestBody Order order) {
        return orderService.addOrder(order);
    }

    @GetMapping("/customer/getOrders/{cid}")
    public ArrayList<OrderView> getCustomerOrders(@PathVariable Integer cid) throws ExecutionException, InterruptedException {
        return orderService.getOrders(cid, 1);
    }

    @GetMapping("/shop/getOrders/{sid}")
    public ArrayList<OrderView> getShopOrders(@PathVariable Integer sid) throws ExecutionException, InterruptedException {
        return orderService.getOrders(sid, 2);
    }

    @GetMapping("/admin/getOrder")
    public CommonResult getAllOrders(@RequestBody SearchOrder searchOrder) {
        return orderService.getOrders(searchOrder, 3);
    }

    @GetMapping("/customer/getOrderStatistics/{cid}")
    public Long[] getCustomerOrderStatistics(@PathVariable Integer cid) {
        return orderService.getCustomerOrderStatistics(cid, 1);
    }
    @GetMapping("/shop/getOrderStatistics/{sid}")
    public Long[] getShopOrderStatistics(@PathVariable Integer sid) {
        return orderService.getCustomerOrderStatistics(sid, 2);
    }
    @GetMapping("/admin/getOrderStatistics/{aid}")
    public Long[] getAllOrderStatistics(@PathVariable Integer aid) {
        return orderService.getCustomerOrderStatistics(aid, 3);
    }

    @GetMapping("/customer/getOrder/{oid}")
    public CommonResult getOrder(@PathVariable String oid) {
        return orderService.getOrder(oid);
    }

    @PostMapping("customer/getOrder")
    public CommonResult getOrders(@RequestBody SearchOrder searchOrder) {
        return orderService.getOrders(searchOrder, 1);
    }

    @PostMapping("shop/getOrder")
    public CommonResult getShopOrders(@RequestBody SearchOrder searchOrder) {
        return orderService.getOrders(searchOrder, 2);
    }

    @PostMapping("customer/deleteOrder")
    public CommonResult deleteOrder(@RequestBody Order order) {
        return orderService.deleteOrder(order);
    }

}
