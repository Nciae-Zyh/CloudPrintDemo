package com.example.printserver.pojo.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class CustomerView {
    @TableId
    private Integer uid;
    private String name;
    private String phoneNumber;
}
