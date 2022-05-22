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

/**
 * 该service为实际应用情况下非支付模式的service，是因为目前没有工商认证特地模拟出的假支付流程
 */
@Service("payService")
public class PayServiceDemo implements PayService {

    OrderMapper orderMapper;
    AlipayConfig alipayConfig;
    ShopMapper shopMapper;
    Sockets sockets;

    @Autowired
    public void setOrderMapper(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Autowired
    public void setAlipayConfig(AlipayConfig alipayConfig) {
        this.alipayConfig = alipayConfig;
    }

    @Autowired
    public void setShopMapper(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    @Autowired
    public void setSockets(Sockets sockets) {
        this.sockets = sockets;
    }

    /**
     * 支付订单
     *
     * @param oid 订单编号
     * @return 支付状态
     */
    @Override
    public CommonResult payOrder(Integer oid) {
        Order order = orderMapper.selectById(oid);
        Timestamp orderTime = order.getOrderTime();
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        // 此方法为判断订单支付状态是否为第一次提交，若第一次提交添加订单时间，若不是则判断是否超时，防止支付失败。
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

    /**
     * 完成订单时的操作
     * 该操作逻辑为从sockets取出一个socket用于传输文件，印打印端采用python书写，所以用最基础的BufferedWriter来传输数据。
     * 同时在打印端续每次一给打印时向表格内添加信息，所以采用中断连接结束进程的方式通过pool获取返回值
     *
     * @param out_tar_no 订单编号
     */
    @Override
    public void finishPayment(String out_tar_no) {
        Integer oid = Integer.valueOf(out_tar_no);
        Order order = orderMapper.selectById(oid);  //在数据库中取出该订单
        order.setPayState(1);   //将订单状态修改为已支付
        /**
         * 新建线程用于传输订单信息给打印端
         */
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
                        // 将文件传输起始标识符输入
                        bufferedWriter.write("1");
                        bufferedWriter.flush();
                        // 防粘包
                        bufferedReader.read();
                        // 传输订单号
                        bufferedWriter.write(String.valueOf(oid));
                        bufferedWriter.flush();
                        bufferedReader.read();
                        bufferedWriter.close();
                        bufferedReader.close();
                        // 关闭本次socket连接
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
