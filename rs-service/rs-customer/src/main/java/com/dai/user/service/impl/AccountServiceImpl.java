package com.dai.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.dai.user.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    /**
     * 获取账号密码登录验证码
     * @return 验证码
     */
    @Override
    public String captcha() {
        return RandomUtil.randomString(4);
    }
}
