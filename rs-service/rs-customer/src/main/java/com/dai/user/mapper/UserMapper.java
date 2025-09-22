package com.dai.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

<<<<<<<< HEAD:rs-service/rs-customer/src/main/java/com/dai/user/mapper/UserMapper.java
    @Select("select * from user where username = #{username}")
    User findByUsername(String username);

    @Select("select * from user where id = #{id}")
    User findByUserId(Long id);

    void updateUser(User user);

    String queryPasswordById(Long aLong);
========
    @Select("select password from user where id = #{id}")
    String queryPasswordById(Long id);

    @Update("update user set password = #{newPassword} where id = #{id}")
    void updatePassword(String newPassword, Long id);
>>>>>>>> origin/master:rs-service/rs-customer/src/main/java/com/dai/user/mapper/AccountMapper.java
}
