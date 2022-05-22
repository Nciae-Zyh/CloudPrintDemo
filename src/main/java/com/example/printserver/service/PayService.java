package com.example.printserver.service;

import com.example.printserver.result.CommonResult;

public interface PayService {
    public CommonResult payOrder(Integer oid);
    public void finishPayment(String out_tar_no);
}
