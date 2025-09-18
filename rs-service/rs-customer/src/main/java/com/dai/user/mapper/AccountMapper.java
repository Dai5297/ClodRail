package com.dai.user.mapper;

import com.dai.model.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AccountMapper {

    @Select("select * from user where username = #{username}")
    User findByUsername(String username);

    @Select("select * from user where id = #{id}")
    User findByUserId(Long id);

    void updateUserInfo(User user);

    @Select("select password from user where id = #{id}")
    String queryPasswordById(Long id);

    @Update("update user set password = #{newPassword} where id = #{id}")
    void updatePassword(String newPassword, Long id);
}
