package com.example.printserver.controller;

import com.example.printserver.pojo.LoginMessage;
import com.example.printserver.pojo.RegisterMessage;
import com.example.printserver.pojo.SocketIp;
import com.example.printserver.pojo.dao.CustomerView;
import com.example.printserver.pojo.dao.ShopView;
import com.example.printserver.pojo.dao.SocketView;
import com.example.printserver.result.CommonResult;
import com.example.printserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    UserService userService;
    @Autowired
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @PostMapping(value = "login")
    public CommonResult login(@RequestBody LoginMessage loginMessage, HttpServletRequest request){
        return userService.login(loginMessage,request);
    }
    @GetMapping("/getShop/{uid}")
    public CommonResult getShop(@PathVariable String uid){
        return CommonResult.success(userService.getShop(uid));

    }

    @PostMapping(value = "shopRegister")
    public CommonResult shopRegister(@RequestBody RegisterMessage shopView){
        return userService.shopRegister(shopView);
    }
    @PostMapping(value = "customerRegister")
    public CommonResult customerRegister(@RequestBody RegisterMessage customerView){
        return userService.customerRegister(customerView);
    }
    @PostMapping(value = "changeShopIp")
    public String changeShopIp(@RequestBody SocketIp socketIp){
        return userService.changeShopIp(socketIp);

    }
}
