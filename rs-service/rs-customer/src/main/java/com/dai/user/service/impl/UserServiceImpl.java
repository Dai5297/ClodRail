package com.dai.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.dai.constant.RedisKeyConstant;
import com.dai.enums.RespCode;
import com.dai.exception.CommonException;
import com.dai.model.domain.User;
import com.dai.user.mapper.UserMapper;
import com.dai.user.model.dto.request.UserInfoUpdateReqDTO;
import com.dai.user.model.dto.request.UserNameLoginReqDTO;
import com.dai.user.model.dto.request.UserResetPasswordReqDTO;
import com.dai.user.model.dto.response.UserInfoResDTO;
import com.dai.user.model.dto.response.UserLoginResDTO;
import com.dai.user.service.UserService;
import com.dai.util.EncoderUtil;
import com.dai.util.JWTUtil;
import com.dai.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 账号密码登录
     * @param reqDTO 登录参数
     * @return 登录结果
     */
    @Override
    public UserLoginResDTO loginByUserName(UserNameLoginReqDTO reqDTO) {
        // 登录校验
        String userName = reqDTO.getUsername();
        User user = userMapper.findByUsername(userName);
        if (ObjectUtil.isEmpty(user)) {
            throw new CommonException(RespCode.DATA_NOT_EXIST, "用户不存在");
        }
        if (!EncoderUtil.matches(reqDTO.getPassword(), user.getPassword())) {
            throw new CommonException(RespCode.SYSTEM_ERROR, "密码错误");
        }
        // 生成token并存储至redis
        String token = JWTUtil.createJWT(JSONUtil.toJsonStr(user));
        UUID uuid = UUID.randomUUID();
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.USER_LOGIN_TOKEN + uuid, token, RedisKeyConstant.USER_LOGIN_TOKEN_TTL, TimeUnit.MILLISECONDS);
        UserLoginResDTO userLoginResDTO = BeanUtil.copyProperties(user, UserLoginResDTO.class);
        userLoginResDTO.setToken(uuid.toString());
        // 存储登录时间
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.USER_LOGIN_TIME + user.getId(), LocalDateTime.now().toString());
        return userLoginResDTO;
    }

    /**
     * 登出
     */
    @Override
    public void logout(String authorization) {
        String uuid = authorization.substring(7);
        Boolean delete = stringRedisTemplate.delete(RedisKeyConstant.USER_LOGIN_TOKEN + uuid);
        if (!BooleanUtil.isTrue(delete)) {
            throw new CommonException(RespCode.SYSTEM_ERROR, "登出失败");
        }
    }

    /**
     * 获取用户信息
     * @return 用户信息
     */
    @Override
    public UserInfoResDTO info() {
        Long id = UserContext.get();
        User user = userMapper.findByUserId(id);
        UserInfoResDTO userInfoResDTO = BeanUtil.toBean(user, UserInfoResDTO.class);
        String loginTime = stringRedisTemplate.opsForValue().get(RedisKeyConstant.USER_LOGIN_TIME + id);
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
        User user = userMapper.findByUserId(reqDTO.getId());
        if (ObjectUtil.isEmpty(user)) {
            throw new CommonException(RespCode.DATA_NOT_EXIST, "用户不存在");
        }
        BeanUtil.copyProperties(reqDTO, user);
        userMapper.updateUser(user);
        return BeanUtil.copyProperties(user, UserInfoResDTO.class);
    }

    /**
     * 获取账号密码登录验证码
     * @return 验证码
     */
    @Override
    public String captcha() {
        return RandomUtil.randomString(4);
    }


    @Override
    public void resetPassword(UserResetPasswordReqDTO reqDTO) {
        String password = userMapper.queryPasswordById(UserContext.get());
        if (!EncoderUtil.matches(reqDTO.getOldPassword(), password)) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "旧密码错误");
        }
        String newPassword = EncoderUtil.encrypt(reqDTO.getNewPassword());
        User user = new User();
        user.setPassword(newPassword);
        user.setId(UserContext.get());
        userMapper.updateUser(user);
    }
}
