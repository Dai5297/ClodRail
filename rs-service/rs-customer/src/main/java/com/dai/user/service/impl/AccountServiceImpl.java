package com.dai.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.dai.user.mapper.AccountMapper;
import com.dai.user.model.dto.request.UserRestPasswordReqDTO;
import com.dai.user.service.AccountService;
import com.dai.enums.RespCode;
import com.dai.exception.CommonException;
import com.dai.util.EncoderUtil;
import com.dai.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;

    /**
     * 获取账号密码登录验证码
     * @return 验证码
     */
    @Override
    public String captcha() {
        return RandomUtil.randomString(4);
    }


    @Override
    public void resetPassword(UserRestPasswordReqDTO reqDTO) {
        // TODO 修该更新时间
        String password = accountMapper.queryPasswordById(UserContext.get());
        if (!EncoderUtil.matches(reqDTO.getOldPassword(), password)) {
            throw new CommonException(RespCode.DATA_NOT_CONSISTENT, "旧密码错误");
        }
        String newPassword = EncoderUtil.encrypt(reqDTO.getNewPassword());
        accountMapper.updatePassword(newPassword, UserContext.get());
    }
}
