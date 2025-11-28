package com.rs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.rs.constant.RedisUserKeyConstant;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.mapper.UserAuthMapper;
import com.rs.model.customer.User;
import com.rs.model.dto.request.user.*;
import com.rs.model.dto.response.user.UserLoginResDTO;
import com.rs.model.dto.response.user.UserRegisterResDTO;
import com.rs.service.UserAuthService;
import com.rs.util.EncoderUtil;
import com.rs.util.JWTUtil;
import com.rs.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final UserAuthMapper userAuthMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 账号密码登录
     *
     * @param reqDTO 登录参数
     * @return 登录结果
     */
    @Override
    public UserLoginResDTO userLoginByUserName(UserNameLoginReqDTO reqDTO) {
        // 登录校验
        String userName = reqDTO.getUsername();
        User user = userAuthMapper.findByUsername(userName);
        if (ObjectUtil.isEmpty(user)) {
            throw new CommonException(RespCode.DATA_NOT_EXIST, "用户不存在");
        }
        if (!EncoderUtil.matches(reqDTO.getPassword(), user.getPassword())) {
            throw new CommonException(RespCode.SYSTEM_ERROR, "密码错误");
        }
        // 生成token并存储至redis
        String token = JWTUtil.createJWT(JSONUtil.toJsonStr(user));
        UUID uuid = UUID.randomUUID();
        stringRedisTemplate.opsForValue().set(RedisUserKeyConstant.USER_LOGIN_TOKEN + uuid, token, RedisUserKeyConstant.USER_LOGIN_TOKEN_TTL, TimeUnit.MILLISECONDS);
        UserLoginResDTO userLoginResDTO = BeanUtil.copyProperties(user, UserLoginResDTO.class);
        userLoginResDTO.setToken(uuid.toString());
        // 存储登录时间
        stringRedisTemplate.opsForValue().set(RedisUserKeyConstant.USER_LOGIN_TIME + user.getId(), LocalDateTime.now().toString());
        return userLoginResDTO;
    }

    /**
     * 登出
     */
    @Override
    public void userLogout(String authorization) {
        String uuid = authorization.substring(7);
        Boolean delete = stringRedisTemplate.delete(RedisUserKeyConstant.USER_LOGIN_TOKEN + uuid);
        if (!BooleanUtil.isTrue(delete)) {
            throw new CommonException(RespCode.SYSTEM_ERROR, "登出失败");
        }
    }

    /**
     * 获取账号密码登录验证码
     *
     * @return 验证码
     */
    @Override
    public String captcha() {
        return RandomUtil.randomString(4);
    }

    /**
     * 重置密码
     *
     * @param reqDTO 重置密码参数
     */
    @Override
    public void userResetPassword(UserResetPasswordReqDTO reqDTO) {
        String password = userAuthMapper.queryPasswordById(UserContext.get());
        if (!EncoderUtil.matches(reqDTO.getOldPassword(), password)) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "旧密码错误");
        }
        String newPassword = EncoderUtil.encrypt(reqDTO.getNewPassword());
        User user = new User();
        user.setPassword(newPassword);
        user.setId(UserContext.get());
        userAuthMapper.updateUserPassword(user);
    }

    /**
     * 注册
     *
     * @param reqDTO 注册参数
     * @return 注册结果
     */
    @Override
    public UserRegisterResDTO userRegister(UserRegisterReqDTO reqDTO) {
        if (!Objects.equals(reqDTO.getCode(), stringRedisTemplate.opsForValue().get(RedisUserKeyConstant.USER_PHONE_CAPTCHA + reqDTO.getPhone()))) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "验证码错误");
        }
        User user = new User();
        BeanUtil.copyProperties(reqDTO, user);
        user.setPassword(EncoderUtil.encrypt(reqDTO.getPassword()));
        userAuthMapper.registerUser(user);
        return BeanUtil.copyProperties(user, UserRegisterResDTO.class);
    }

    @Override
    public void captchaPhone(String phone) {
        int captcha = RandomUtil.randomInt(100000, 999999);
        log.info("手机验证码：{}", captcha);
        String key = RedisUserKeyConstant.USER_PHONE_CAPTCHA + phone;
        stringRedisTemplate.opsForValue().set(key, String.valueOf(captcha), RedisUserKeyConstant.USER_PHONE_CAPTCHA_TTL, TimeUnit.SECONDS);
    }

    /**
     * 修改手机号
     *
     * @param userPhoneChangeReqDTO 修改手机号参数
     */
    @Override
    public void userChangePhone(UserPhoneChangeReqDTO userPhoneChangeReqDTO) {
        if (!Objects.equals(userPhoneChangeReqDTO.getCode(), stringRedisTemplate.opsForValue().get(RedisUserKeyConstant.USER_PHONE_CAPTCHA + userPhoneChangeReqDTO.getNewPhone()))) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "验证码错误");
        }
        Long userId = UserContext.get();
        userAuthMapper.updatePhone(userId, userPhoneChangeReqDTO.getNewPhone());
    }

    /**
     * 获取邮箱修改验证码
     *
     * @param email 邮箱
     */
    @Override
    public void emailChangeCode(String email) {
        int code = RandomUtil.randomInt(100000, 999999);
        log.info("邮箱验证码：{}", code);
        stringRedisTemplate.opsForValue().set(RedisUserKeyConstant.USER_EMAIL_CODE + email, String.valueOf(code), RedisUserKeyConstant.USER_EMAIL_CHANGE_CODE_TTL, TimeUnit.SECONDS);
    }

    @Override
    public void userChangeEmail(UserEmailChangeReqDTO userEmailChangeReqDTO) {
        if (!Objects.equals(userEmailChangeReqDTO.getCode(), stringRedisTemplate.opsForValue().get(RedisUserKeyConstant.USER_EMAIL_CODE + userEmailChangeReqDTO.getNewEmail()))) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "验证码错误");
        }
        userAuthMapper.updateEmail(UserContext.get(), userEmailChangeReqDTO.getNewEmail());
    }
}