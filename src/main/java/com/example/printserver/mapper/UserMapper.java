package com.example.printserver.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.example.printserver.pojo.dao.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user ${ew.customSqlSegment}")
    List<User> selectByMyWrapper(@Param(Constants.WRAPPER) Wrapper<User> userWrapper);

    @Select("select * from user where phone_number = #{phone_number}")
    User selectByPhoneNumber(@Param("phone_number") String phoneNumber);
}
