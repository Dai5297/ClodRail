package com.rs.service;

import com.rs.model.PageResult;
import com.rs.model.dto.request.admin.AdminSaveReqDTO;
import com.rs.model.dto.response.admin.AdminProfileResDTO;

public interface AdminService {

    PageResult<AdminProfileResDTO> adminPage(String username,
            String realName,
            Integer status,
            Integer pageNum,
            Integer pageSize);

    void updateStatus(Long id, Integer status);

    void resetPassword(Long id);

    void updateRole(Long id, Integer role);

    void save(AdminSaveReqDTO reqDTO);
}
