package com.example.printserver.pojo;

import lombok.Data;

@Data
public class AlipayObject {
    private String out_trade_no;
    private double total_amount;
    private String subject;
}
