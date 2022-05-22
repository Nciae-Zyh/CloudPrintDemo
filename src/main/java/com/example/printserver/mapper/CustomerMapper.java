package com.example.printserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.printserver.pojo.dao.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

}
