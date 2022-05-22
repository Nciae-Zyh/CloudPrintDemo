package com.example.printserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.printserver.pojo.dao.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
