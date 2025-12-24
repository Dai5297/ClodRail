package com.rs.service;

import com.rs.model.dto.request.AdminProfileReqDTO;
import com.rs.model.dto.response.admin.AdminProfileResDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AdminProfileService {

    /**
     * 获取管理员信息
     *
     * @return 管理员信息
     */
    AdminProfileResDTO info();

    /**
     * 修改管理员头像
     *
     * @param file 头像文件
     * @return 头像地址
     */
    String updateIcon(MultipartFile file);

    /**
     * 修改管理员信息
     *
     * @param reqDTO 修改信息
     */
    void updateInfo(AdminProfileReqDTO reqDTO);
}
