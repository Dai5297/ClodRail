package com.rs.service;

import com.rs.model.dto.request.user.UserInfoUpdateReqDTO;
import com.rs.model.dto.response.user.UserInfoResDTO;

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
}
