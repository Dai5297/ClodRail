package com.rs.mapper;

import com.rs.model.customer.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {

    Admin queryAdminById(Long id);

    Admin queryAdminByUsername(String username);

    void updateIcon(Long id, String icon);

    void updateInfo(Admin admin);
}
