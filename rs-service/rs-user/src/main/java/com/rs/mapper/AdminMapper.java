package com.rs.mapper;

import com.rs.model.customer.Admin;
import com.rs.model.dto.response.admin.AdminProfileResDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper {

    Admin queryAdminById(Long id);

    Admin queryAdminByUsername(String username);

    void updateIcon(Long id, String icon);

    void updateInfo(Admin admin);

    void insert(Admin admin);

    List<AdminProfileResDTO> adminPage(@Param("username") String username,
            @Param("realName") String realName,
            @Param("status") Integer status);

    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    void updateRole(@Param("id") Long id, @Param("role") Integer role);

    void updatePassword(@Param("id") Long id, @Param("password") String password);
}
