package com.example.printserver.controller;

import com.example.printserver.pojo.dao.Order;
import com.example.printserver.result.CommonResult;
import com.example.printserver.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class PayController {

    @Autowired
    PayService payService;

    @ResponseBody
    @PostMapping("/pay/payOrder")
    public CommonResult payOrder(@RequestBody Order id) {
        return payService.payOrder(id.getOid());
    }
    @PostMapping("/pay/notifyUrl")
    public void notifyUrl(HttpServletRequest request, HttpServletResponse response) {
        String out_trade_no = null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String s : parameterMap.keySet()) {
            String[] strings = parameterMap.get(s);
            for (int i = 0; i < strings.length; i++) {
                if (s.equals("out_trade_no")) {
                    out_trade_no = strings[i];
                    break;
                }
            }
        }
        payService.finishPayment(out_trade_no);
    }
    @RequestMapping("/pay/returnUrl")
    public String returnUrl(HttpServletRequest request, HttpServletResponse response) {
        return "<script>" +
                "window.opener = null;" +
                "window.open(\"\", \"_self\");" +
                "window.close();" +
                "</script>";
    }
}
