package com.example.printserver.pojo.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("order_table")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer oid;
    private Integer shopId;
    private Integer customerId;
    private String filename;
    private Integer color;
    private Integer duplex;
    private Integer printNum;
    private Integer payState;
    private Integer orderState;
    private Integer orderPage;
    private double price;
    private Timestamp orderTime;
}
