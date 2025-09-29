package com.dai.mapper;

import com.dai.model.customer.User;
import com.dai.model.dto.response.UserInfoResDTO;
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
}
