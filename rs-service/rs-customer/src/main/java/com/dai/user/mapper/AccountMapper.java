package com.dai.user.mapper;

import com.dai.user.model.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountMapper {

    @Select("select * from user where username = #{username}")
    User findByUsername(String username);
}
