package com.dai.mapper;

import com.dai.model.domain.customer.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 根据ID查询密码
     * @param id 用户ID
     * @return 密码
     */
    String queryPasswordById(Long id);

    /**
     * 更新用户密码
     * @param user 用户信息（包含ID和新密码）
     */
    void updateUserPassword(User user);

    /**
     * 注册用户
     * @param user 用户信息
     */
    void registerUser(User user);
}