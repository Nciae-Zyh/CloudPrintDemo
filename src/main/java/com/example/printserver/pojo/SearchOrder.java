package com.example.printserver.pojo;

import lombok.Data;

@Data
public class SearchOrder {
    private String namePart;
    private Integer id;
    private Integer[] duplex;
    private Integer[] color;
    private Integer[] payState;
    private Integer[] orderState;
}
