package com.rs.mapper;

import com.rs.model.customer.AdminPermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionMapper {

    List<AdminPermission> queryPermission(Integer role);

    List<String> queryRoutes(Integer role);

    List<AdminPermission> queryAllApis();
}
