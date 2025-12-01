package com.rs.service;

import com.rs.model.dto.request.user.UserInfoUpdateReqDTO;
import com.rs.model.dto.response.user.UserInfoResDTO;

import java.util.List;
import java.util.Map;

public interface UserService {

    /**
     * 获取用户信息
     * @return 用户信息
     */
    UserInfoResDTO info();

    /**
     * 修改用户信息
     * @param reqDTO 修改参数
     * @return 修改结果
     */
    UserInfoResDTO updateInfo(UserInfoUpdateReqDTO reqDTO);

    /**
     * 获取用户名称列表
     * @param userIds 用户id列表
     * @return 用户名称列表
     */
    Map<Long, String> usernameList(List<Long> userIds);
}
