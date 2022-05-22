package com.example.printserver.util;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.example.printserver.config.AlipayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AliPayUtil {
    @Autowired
    AlipayConfig alipayConfig;
    public AlipayClient getAlipayClient(){
        return new DefaultAlipayClient(alipayConfig.getGatewayUrl(),alipayConfig.getApp_id(),alipayConfig.getMerchant_private_key(),"json",alipayConfig.getCharset(),alipayConfig.getAlipay_public_key(),alipayConfig.getSign_type());
    }
}
