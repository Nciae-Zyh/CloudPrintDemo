package com.example.printserver.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
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
 * 该类为生产场景下的alipay接口实现，目前因无工商资质未使用
 */
//@Service("payService")
public class PayServiceImpl implements PayService {

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
     * 通过oid查询交易的订单号，从而达到填充支付详细信息的功能
     *
     * @param oid 订单号
     * @return 返回一个表单，前端调用该表单来跳转到支付宝支付页面。
     */
    @Override
    public CommonResult payOrder(Integer oid) {
        Order order = orderMapper.selectById(oid);  //根据oid检索order
        Timestamp orderTime = order.getOrderTime(); //通过order中是否有orderTime来判断该订单是否为第一次提交。
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
        //订单信息的填充
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", order.getOid());
        bizContent.put("subject", order.getFilename());
        bizContent.put("total_amount", order.getPrice());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        bizContent.put("timeout_express", "10m");
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getGatewayUrl(), alipayConfig.getApp_id(), alipayConfig.getMerchant_private_key(), "json", alipayConfig.getCharset(), alipayConfig.getAlipay_public_key(), alipayConfig.getSign_type());
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotify_url());
        request.setReturnUrl(alipayConfig.getReturn_url());
        request.setBizContent(bizContent.toString());
        String response = null;
        try {
            //表单
            response = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return CommonResult.failed();
        }
        return CommonResult.success(response, "沙盒测试");
    }

    /**
     * 完成订单的操作，应用于支付宝支付成功回调地址
     *
     * @param out_tar_no 订单编号
     */
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
