package com.example.printserver.socket;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.printserver.mapper.OrderMapper;
import com.example.printserver.mapper.ShopMapper;
import com.example.printserver.pojo.Sockets;
import com.example.printserver.pojo.dao.Order;
import com.example.printserver.pojo.dao.Shop;
import com.example.printserver.result.CommonResult;
import com.example.printserver.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Callable;

@Slf4j
public class SendFileServe implements Callable {
    private Sockets sockets;

    Integer uid;
    String address;
    String filepath;
    Integer printNum;
    Integer color;
    Integer duplex;

    OrderMapper orderMapper;

    ShopMapper shopMapper;

    private FileInputStream fis;

    private DataOutputStream dos;

    public SendFileServe(Integer uid, String address, String filepath, Integer printNum, Integer color, Integer duplex) {
        this.uid = uid;
        this.address = address;
        this.color = color;
        this.filepath = filepath;
        this.printNum = printNum;
        this.duplex = duplex;
        sockets = SpringContextUtil.getBean(Sockets.class);
        shopMapper = SpringContextUtil.getBean(ShopMapper.class);
        orderMapper = SpringContextUtil.getBean(OrderMapper.class);
    }

    @Override
    public synchronized Object call() {
        if (sendFile()) {
            Order order = new Order();
            order.setCustomerId(uid);
            QueryWrapper<Shop> wrapper = new QueryWrapper();
            wrapper.eq("socket_address", address);
            Shop shop = shopMapper.selectOne(wrapper);
            order.setShopId(shop.getUid());
            order.setPrintNum(printNum);
            order.setDuplex(duplex);
            order.setColor(color);
            String filename = filepath.substring(filepath.lastIndexOf('/') + 1);
            order.setFilename(filename);
            orderMapper.insert(order);
            return CommonResult.success("????????????!");
        }
        return CommonResult.failed("???????????????");
    }
//    @Override
//    public Object call(){
//        Socket socket = null;
//
//        return CommonResult.success(1);
//    }

    public synchronized Boolean sendFile() {
        Socket socket = null;
        try {
            Integer workedNum = sockets.getWorkedSocket().get(address);
            sockets.getWorkedSocket().replace(address, workedNum + 1);
            socket = sockets.getSockets().get(address).get(0);
            if (socket != null) {
                sockets.getSockets().get(address).remove(0);
            }
            File file = new File(filepath);
            if (file.exists()) {
                OutputStream out = socket.getOutputStream();
                fis = new FileInputStream(file);
                dos = new DataOutputStream(socket.getOutputStream());
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                /**
                 * ???????????????OutputStream????????????????????????
                 * ??????????????????
                 * 1.?????????
                 * 2.?????????
                 * 3.????????????
                 * 4.????????????
                 * 5.????????????
                 * 6.?????????
                 * 7.???????????????
                 */
                bufferedWriter.write("1");
                bufferedWriter.flush();
                bufferedReader.read();
                bufferedWriter.write(String.valueOf(uid));
                bufferedWriter.flush();
                bufferedReader.read();
                bufferedWriter.write(file.getName());
                bufferedWriter.flush();
                bufferedReader.read();
                bufferedWriter.write(String.valueOf(file.length()));
                bufferedWriter.flush();
                bufferedReader.read();
                bufferedWriter.write(String.valueOf(printNum));
                bufferedWriter.flush();
                bufferedReader.read();
                bufferedWriter.write(String.valueOf(color));
                bufferedWriter.flush();
                bufferedReader.read();
                bufferedWriter.write(String.valueOf(duplex));
                bufferedWriter.flush();
                bufferedReader.read();
                byte[] bytes = new byte[1024];
                int length = 0;
                while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    out.write(bytes, 0, length);
                    out.flush();
                    bufferedReader.read();
                }
                InputStream inputStream = socket.getInputStream();
                inputStream.read();
                out.close();
                bufferedWriter.close();
                inputStream.close();
                socket.close();
            } else {
                int i = 0 / 0;
            }
        } catch (Exception exception01) {
            exception01.printStackTrace();
            return false;
        } finally {
            Integer workedNum = sockets.getWorkedSocket().get(address);
            sockets.getWorkedSocket().replace(address, workedNum - 1);
        }
        try {
            assert dos != null;
            dos = null;
            assert fis != null;
            fis = null;
            socket = null;
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

}
