package com.example.printserver.pojo;

import lombok.Data;

@Data
public class SendOrder {
    private Integer uid;
    private String address;
    private String filename;
    private Integer printNum;
    private Integer color;
    private Integer duplex;
}
