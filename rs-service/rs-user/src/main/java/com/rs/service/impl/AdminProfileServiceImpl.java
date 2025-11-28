package com.rs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.rs.mapper.AdminMapper;
import com.rs.model.customer.Admin;
import com.rs.model.dto.request.AdminProfileReqDTO;
import com.rs.model.dto.response.admin.AdminProfileResDTO;
import com.rs.service.AdminProfileService;
import com.rs.util.OssUtil;
import com.rs.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminProfileServiceImpl implements AdminProfileService {

    private final AdminMapper adminMapper;

    private final OssUtil ossUtil;

    @Override
    public AdminProfileResDTO info() {
        Admin admin = adminMapper.queryAdminById(UserContext.get());
        AdminProfileResDTO adminProfileResDTO = new AdminProfileResDTO();
        BeanUtil.copyProperties(admin, adminProfileResDTO);
        return adminProfileResDTO;
    }

    @Override
    public String updateIcon(MultipartFile file) {
        String upload = ossUtil.upload(file);
        adminMapper.updateIcon(UserContext.get(), upload);
        return upload;
    }

    @Override
    public void updateInfo(AdminProfileReqDTO reqDTO) {
        Admin admin = BeanUtil.toBean(reqDTO, Admin.class);
        adminMapper.updateInfo(admin);
    }
}
