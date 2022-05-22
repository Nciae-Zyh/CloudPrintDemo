package com.example.printserver.pojo.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ShopView {
    @TableId
    private Integer uid;
    private String name;
    private String phoneNumber;
    private String address;
    private String socketAddress;
    private Boolean isColor;
    private Boolean isDuplex;
}
