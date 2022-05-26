package com.example.printserver.service;

import com.example.printserver.pojo.LoginMessage;
import com.example.printserver.pojo.RegisterMessage;
import com.example.printserver.pojo.SocketIp;
import com.example.printserver.pojo.dao.CustomerView;
import com.example.printserver.pojo.dao.ShopView;
import com.example.printserver.pojo.dao.SocketView;
import com.example.printserver.result.CommonResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    public CommonResult login(LoginMessage loginMessage, HttpServletRequest request);
    public CommonResult customerRegister(RegisterMessage customer);
    public CommonResult shopRegister(RegisterMessage shopView);
    public ShopView getShop(String uid);

    String changeShopIp(SocketIp socketIp);
}
