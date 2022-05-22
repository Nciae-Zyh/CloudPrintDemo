package com.example.printserver.pojo.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class SocketView {
    @TableId
    private String name;
    private Boolean isColor;
    private Boolean isDuplex;
    private String socketAddress;
}
