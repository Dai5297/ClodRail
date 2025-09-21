package com.dai.user.mapper;

import com.dai.model.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username}")
    User findByUsername(String username);

    @Select("select * from user where id = #{id}")
    User findByUserId(Long id);

    void updateUserInfo(User user);

}
