package com.rs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.model.customer.User;
import com.rs.mapper.UserMapper;
import com.rs.model.dto.request.user.UserInfoUpdateReqDTO;
import com.rs.model.dto.response.user.UserInfoResDTO;
import com.rs.service.UserService;
import com.rs.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 获取用户信息
     * @return 用户信息
     */
    @Override
    public UserInfoResDTO info() {
        Long id = UserContext.get();
        UserInfoResDTO userInfoResDTO = userMapper.queryInfoById(id);
        String loginTime = stringRedisTemplate.opsForValue().get("user:login:time:" + id);
        if (loginTime != null) {
            LocalDateTime lastLoginTime = LocalDateTime.parse(loginTime);
            userInfoResDTO.setLastLoginTime(lastLoginTime);
        }
        return userInfoResDTO;
    }

    /**
     * 修改用户信息
     * @param reqDTO 修改参数
     * @return 修改结果
     */
    @Override
    public UserInfoResDTO updateInfo(UserInfoUpdateReqDTO reqDTO) {
        User user = userMapper.queryById(reqDTO.getId());
        if (ObjectUtil.isEmpty(user)) {
            throw new CommonException(RespCode.DATA_NOT_EXIST, "用户不存在");
        }
        BeanUtil.copyProperties(reqDTO, user);
        userMapper.updateUser(user);
        return BeanUtil.copyProperties(user, UserInfoResDTO.class);
    }

    @Override
    public Map<Long, String> usernameList(List<Long> userIds) {
        Map<Long, String> usernameMap = new HashMap<>();
        userIds.stream().filter(Objects::nonNull).forEach(id -> {
            String username = userMapper.queryUsername(id);
            usernameMap.put(id, username);
        });
        return usernameMap;
    }
}