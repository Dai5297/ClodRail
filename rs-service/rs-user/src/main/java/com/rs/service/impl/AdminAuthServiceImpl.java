package com.rs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.mapper.AdminMapper;
import com.rs.mapper.PermissionMapper;
import com.rs.model.customer.Admin;
import com.rs.model.dto.request.admin.AdminLoginReqDTO;
import com.rs.model.dto.response.admin.AdminLoginResDTO;
import com.rs.service.AdminAuthService;
import com.rs.util.EncoderUtil;
import com.rs.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.rs.constant.CommonConstant.AUTH_PREFIX;
import static com.rs.constant.RedisUserKeyConstant.ADMIN_LOGIN_TOKEN;
import static com.rs.constant.RedisUserKeyConstant.USER_LOGIN_TOKEN_TTL;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminMapper adminMapper;

    private final PermissionMapper permissionMapper;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public AdminLoginResDTO login(AdminLoginReqDTO reqDTO) {
        Admin admin = adminMapper.queryAdminByUsername(reqDTO.getUsername());
        if (admin == null) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "用户不存在");
        }
        if (!EncoderUtil.matches(reqDTO.getPassword(), admin.getPassword())) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "密码错误");
        }
        String jwt = JWTUtil.createJWT(JSONUtil.toJsonStr(admin));
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(ADMIN_LOGIN_TOKEN + uuid, jwt, USER_LOGIN_TOKEN_TTL, TimeUnit.MILLISECONDS);
        AdminLoginResDTO adminLoginResDTO = new AdminLoginResDTO();
        BeanUtil.copyProperties(admin, adminLoginResDTO);
        adminLoginResDTO.setToken(uuid);
        adminLoginResDTO.setRoutes(permissionMapper.queryRoutes(admin.getRole()));
        return adminLoginResDTO;
    }

    @Override
    public void logout(String token) {
        String uuid = token.substring(AUTH_PREFIX.length());
        stringRedisTemplate.delete(ADMIN_LOGIN_TOKEN + uuid);
    }
}
