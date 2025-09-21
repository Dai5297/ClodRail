package com.dai.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AccountMapper {

    @Select("select password from user where id = #{id}")
    String queryPasswordById(Long id);

    @Update("update user set password = #{newPassword} where id = #{id}")
    void updatePassword(String newPassword, Long id);
}
