package com.dai.service;

import com.dai.model.dto.request.UserInfoUpdateReqDTO;
import com.dai.model.dto.response.UserInfoResDTO;

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
