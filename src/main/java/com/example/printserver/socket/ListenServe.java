package com.example.printserver.socket;

import com.example.printserver.mapper.ShopMapper;
import com.example.printserver.mapper.SocketViewMapper;
import com.example.printserver.pojo.Sockets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

@Slf4j
@Component
public class ListenServe {

    protected Sockets sockets;

    @Autowired
    public void setSockets(Sockets sockets) {
        this.sockets = sockets;
    }

    @Autowired
    private ShopMapper shopMapper;
    private SocketViewMapper socketViewMapper;
    @Autowired
    public void setSocketViewMapper(SocketViewMapper socketViewMapper){
        this.socketViewMapper = socketViewMapper;
    }
    private ServerSocket serverSocket;

    public ListenServe() throws IOException {
        int socketPort = 33019;
        serverSocket = new ServerSocket(socketPort);
        new Thread(new ListenLink()).start();
    }

    class ListenLink implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(new Listen(socket)).start();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    class Listen implements Runnable {
        Socket socket;

        public Listen(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            if (sockets.getSockets().get(socket.getInetAddress().getHostAddress()) == null) {
                /**
                 * 此处为将ip地址存入到数据库中
                 */
                System.out.println(socket.getInetAddress().getHostAddress());
                sockets.getSockets().put(socket.getInetAddress().getHostAddress(), new ArrayList<Socket>());
                sockets.getWorkedSocket().put(socket.getInetAddress().getHostAddress(), 0);
            }
            ArrayList<Socket> list = sockets.getSockets().get(socket.getInetAddress().getHostAddress());
            list.add(socket);
        }
    }
}


