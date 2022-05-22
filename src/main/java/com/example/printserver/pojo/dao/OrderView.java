package com.example.printserver.pojo.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("order_view")
public class OrderView {
    @TableId
    private Integer oid;
    private Integer customerId;
    private Integer shopId;
    private String customerName;
    private String shopName;
    private String address;
    private String filename;
    private Integer color;
    private Integer duplex;
    private Integer printNum;
    private Integer payState;
    private Integer orderState;
    private Integer orderPage;
    private double price;
    private String phoneNumber;
    private Timestamp orderTime;
}
