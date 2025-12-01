package com.rs.mapper;

import com.rs.model.customer.User;
import com.rs.model.dto.response.user.UserInfoResDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    User queryById(Long id);

    /**
     * 根据ID查询用户信息
     * @param id 用户ID
     * @return 用户信息DTO
     */
    UserInfoResDTO queryInfoById(Long id);

    /**
     * 更新用户信息
     * @param user 用户信息
     */
    void updateUser(User user);

    /**
     * 根据ID查询用户名
     * @param id 用户ID
     * @return 用户名
     */
    String queryUsername(Long id);
}
