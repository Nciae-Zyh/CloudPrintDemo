package com.example.printserver.pojo.dao;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Shop {
    @TableId
    private Integer uid;
    private String name;
    private Boolean isColor;
    private Boolean isDuplex;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String socketAddress;
}
