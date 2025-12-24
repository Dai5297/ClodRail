package com.rs.service;

import com.rs.model.dto.request.admin.AdminLoginReqDTO;
import com.rs.model.dto.response.admin.AdminLoginResDTO;

public interface AdminAuthService {

    AdminLoginResDTO login(AdminLoginReqDTO reqDTO);

    void logout(String token);
}
