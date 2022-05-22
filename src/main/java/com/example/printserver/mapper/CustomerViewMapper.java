package com.example.printserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.printserver.pojo.dao.CustomerView;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerViewMapper extends BaseMapper<CustomerView> {
}
