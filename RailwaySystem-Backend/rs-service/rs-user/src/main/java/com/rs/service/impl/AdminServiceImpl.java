package com.rs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.mapper.AdminMapper;
import com.rs.model.PageResult;
import com.rs.model.customer.Admin;
import com.rs.model.dto.request.admin.AdminSaveReqDTO;
import com.rs.model.dto.response.admin.AdminProfileResDTO;
import com.rs.service.AdminService;
import com.rs.util.EncoderUtil;
import com.rs.util.PageUtil;
import com.rs.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;

    @Override
    public PageResult<AdminProfileResDTO> adminPage(String username,
            String realName,
            Integer status,
            Integer pageNum,
            Integer pageSize) {
        PageUtil.startPage(pageNum, pageSize);
        List<AdminProfileResDTO> list = adminMapper.adminPage(username, realName, status);
        return PageUtil.buildPageResult(list);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Admin admin = adminMapper.queryAdminById(id);
        if (admin == null) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "管理员不存在");
        }
        adminMapper.updateStatus(id, status);
    }

    @Override
    public void resetPassword(Long id) {
        Admin admin = adminMapper.queryAdminById(id);
        if (admin == null) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "管理员不存在");
        }
        String encrypted = EncoderUtil.encrypt("123456");
        adminMapper.updatePassword(id, encrypted);
    }

    @Override
    public void updateRole(Long id, Integer role) {
        Admin admin = adminMapper.queryAdminById(id);
        if (admin == null) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "管理员不存在");
        }
        String roleName = getRoleName(role);
        if (roleName == null) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "角色不合法");
        }
        adminMapper.updateRole(id, role);
    }

    private String getRoleName(Integer role) {
        if (role == null) {
            return null;
        }
        return switch (role) {
            case 104 -> "超级管理员";
            case 103 -> "运营管理员";
            case 102 -> "财务管理员";
            case 101 -> "客服管理员";
            case 100 -> "管理员";
            default -> null;
        };
    }

    @Override
    public void save(AdminSaveReqDTO reqDTO) {
        if (reqDTO.getId() == null) {
            Admin exists = adminMapper.queryAdminByUsername(reqDTO.getUsername());
            if (exists != null) {
                throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "用户名已存在");
            }
            Admin admin = BeanUtil.toBean(reqDTO, Admin.class);
            Integer role = reqDTO.getRole() == null ? 100 : reqDTO.getRole();
            String roleName = getRoleName(role);
            if (roleName == null) {
                throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "角色不合法");
            }
            admin.setRole(role);
            admin.setStatus(reqDTO.getStatus() == null ? 1 : reqDTO.getStatus());
            admin.setPassword(EncoderUtil.encrypt("123456"));
            admin.setCreateBy(UserContext.get());
            admin.setUpdateBy(UserContext.get());
            adminMapper.insert(admin);
        } else {
            Admin admin = adminMapper.queryAdminById(reqDTO.getId());
            if (admin == null) {
                throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "管理员不存在");
            }
            Admin toUpdate = BeanUtil.toBean(reqDTO, Admin.class);
            if (reqDTO.getRole() != null) {
                String roleName = getRoleName(reqDTO.getRole());
                if (roleName == null) {
                    throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "角色不合法");
                }
            }
            adminMapper.updateInfo(toUpdate);
        }
    }
}
