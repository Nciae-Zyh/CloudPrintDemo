package com.example.printserver.socket;

import com.example.printserver.mapper.OrderMapper;
import com.example.printserver.mapper.ShopMapper;
import com.example.printserver.pojo.Sockets;
import com.example.printserver.pojo.dao.Order;
import com.example.printserver.pojo.dao.Shop;
import com.example.printserver.result.CommonResult;
import com.example.printserver.util.SpringContextUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

public class SendOrderServe implements Callable {
    private Sockets sockets;
    private OrderMapper orderMapper;
    private ShopMapper shopMapper;
    Integer oid;

    public SendOrderServe(Integer oid) {
        this.oid = oid;
        sockets = SpringContextUtil.getBean(Sockets.class);
        orderMapper = SpringContextUtil.getBean(OrderMapper.class);
        shopMapper = SpringContextUtil.getBean(ShopMapper.class);
    }

    @Override
    public Object call() throws Exception {
//        Socket socket = null;
//        try {
//            Order order = orderMapper.selectById(oid);
//            Shop shop = shopMapper.selectById(order.getShopId());
//            String address;
//            if ((address = shop.getSocketAddress()) != null) {
//                Integer workedNum = sockets.getWorkedSocket().get(address);
//                sockets.getWorkedSocket().replace(address, workedNum + 1);
//                socket = sockets.getSockets().get(address).get(0);
//                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                bufferedWriter.write(String.valueOf(oid));
//                bufferedReader.read();
//                return CommonResult.success(oid);
//            } else {
//                return CommonResult.success(oid);
//            }
////            Integer workedNum = sockets.getWorkedSocket()
//        } catch (Exception e) {
//
//        }
        return null;
    }
}
