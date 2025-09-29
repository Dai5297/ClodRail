package com.dai.mapper;

import com.dai.model.customer.User;
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

    /**
     * 更新用户手机号
     * @param id 用户ID
     * @param newPhone 新手机号
     */
    void updatePhone(Long id, String newPhone);

    /**
     * 更新用户邮箱
     * @param id 用户ID
     * @param newEmail 新邮箱
     */
    void updateEmail(Long id, String newEmail);
}