package com.example.printserver.socket;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.printserver.mapper.ShopMapper;
import com.example.printserver.pojo.Sockets;
import com.example.printserver.pojo.dao.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@Component
@Slf4j
public class CheckConnectionServe {

    private Sockets sockets;

    private ShopMapper shopMapper;

    @Autowired
    public void setShopMapper(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    @Autowired
    public void setSockets(Sockets sockets) {
        this.sockets = sockets;
    }

    public CheckConnectionServe() {
        new Thread(new CheckConnection()).start();
    }

    class CheckConnection implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                HashMap<String, ArrayList<Socket>> socketMap = sockets.getSockets();
                Set<String> socketSet = socketMap.keySet();
                assert socketSet != null;
                for (String key : socketSet) {
                    ArrayList<Socket> list = socketMap.get(key);
                    if (checkNum(socketMap, key, list)) break;
                    synchronized (this) {
                        for (int i = 0; i < list.size(); ) {
                            Socket socket = list.get(i);
                            OutputStream outputStream = null;
                            InputStream inputStream = null;
                            try {
                                outputStream = socket.getOutputStream();
                                inputStream = socket.getInputStream();
                            } catch (Exception ignored) {

                            }
                            try {
                                byte[] bytes = "0".getBytes(StandardCharsets.UTF_8);
                                assert outputStream != null;
                                outputStream.write(bytes);
                                outputStream.flush();
                                assert inputStream != null;
                                inputStream.read(bytes);
                                i++;
                            } catch (Exception e) {
                                list.remove(i);
                                if (checkNum(socketMap, key, list)) break;
                            }
                        }
                    }
                }
                /**
                 * 遍历间隔
                 */
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private boolean checkNum(HashMap<String, ArrayList<Socket>> socketMap, String key, ArrayList<Socket> list) {
            if (list.size() == 0 && sockets.getWorkedSocket().get(key) == 0) {
                log.warn("连接失效！即将删除！");
                socketMap.remove(key);
                QueryWrapper wrapper = new QueryWrapper();
                wrapper.eq("socket_address", key);
                Shop shop = shopMapper.selectOne(wrapper);
                if (shop != null) {
                    shop.setSocketAddress(null);
                    shopMapper.updateById(shop);
                }
                return true;
            }
            return false;
        }
    }
}
