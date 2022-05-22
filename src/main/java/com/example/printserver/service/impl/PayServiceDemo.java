package com.example.printserver.service.impl;

import com.example.printserver.config.AlipayConfig;
import com.example.printserver.mapper.OrderMapper;
import com.example.printserver.mapper.ShopMapper;
import com.example.printserver.pojo.Sockets;
import com.example.printserver.pojo.dao.Order;
import com.example.printserver.pojo.dao.Shop;
import com.example.printserver.result.CommonResult;
import com.example.printserver.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;

@Service("payService")
public class PayServiceDemo implements PayService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    AlipayConfig alipayConfig;
    @Autowired
    ShopMapper shopMapper;
    @Autowired
    Sockets sockets;

    @Override
    public CommonResult payOrder(Integer oid) {
        Order order = orderMapper.selectById(oid);
        Timestamp orderTime = order.getOrderTime();
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        if (orderTime == null) {
            order.setOrderTime(timestamp);
            orderMapper.updateById(order);
        } else {
            Long s = (timestamp.getTime() - orderTime.getTime()) / (1000 * 60);
            int minutes = s.intValue();
            if (minutes > 10) {
                return CommonResult.failed("订单已过期！");
            }
        }
        finishPayment(oid.toString());
        return CommonResult.success("成功支付（大概吧）", "无支付测试");
    }

    @Override
    public void finishPayment(String out_tar_no) {
        Integer oid = Integer.valueOf(out_tar_no);
        Order order = orderMapper.selectById(oid);
        order.setPayState(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                Order order;
                Shop shop;
                String address = null;
                try {
                    order = orderMapper.selectById(oid);
                    shop = shopMapper.selectById(order.getShopId());
                    if ((address = shop.getSocketAddress()) != null) {
                        Integer workedNum = sockets.getWorkedSocket().get(address);
                        sockets.getWorkedSocket().replace(address, workedNum + 1);
                        socket = sockets.getSockets().get(address).get(0);
                        if (socket != null) {
                            sockets.getSockets().get(address).remove(0);
                        }
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        bufferedWriter.write("1");
                        bufferedWriter.flush();
                        bufferedReader.read();
                        bufferedWriter.write(String.valueOf(oid));
                        bufferedWriter.flush();
                        bufferedReader.read();
                        bufferedWriter.close();
                        bufferedReader.close();
                        socket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (address != null) {
                        Integer workedNum = sockets.getWorkedSocket().get(address);
                        sockets.getWorkedSocket().replace(address, workedNum - 1);
                    }
                }
            }
        }).start();
        orderMapper.updateById(order);
    }
}
