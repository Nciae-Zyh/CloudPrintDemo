package com.example.printserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.printserver.mapper.ShopViewMapper;
import com.example.printserver.mapper.SocketViewMapper;
import com.example.printserver.pojo.dao.ShopView;
import com.example.printserver.pojo.dao.SocketView;
import com.example.printserver.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class IpController {
    @Autowired
    SocketViewMapper socketViewMapper;
    @Autowired
    ShopViewMapper shopViewMapper;
    @GetMapping("/getIp")
    public String getIp(HttpServletRequest request){
        return IPUtils.getIpAddr(request);
    }

    /**
     * 获取shop的列表
     * @return  返回json型的shop sockets
     */
    @GetMapping("/getShops")
    public List<ShopView> getShops(){
        return shopViewMapper.selectList(null);
    }
}
