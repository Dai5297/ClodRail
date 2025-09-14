package com.dai.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.dai.user.mapper.AccountMapper;
import com.dai.user.model.domain.User;
import com.dai.user.model.dto.request.UserNameLoginReqDTO;
import com.dai.user.model.dto.response.UserLoginResDTO;
import com.dai.user.service.AccountService;
import com.dai.enums.RespCode;
import com.dai.exception.CommonException;
import com.dai.util.EncoderUtils;
import lombok.RequiredArgsConstructor;
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

    /**
     * 账号密码登录
     * @param reqDTO 登录参数
     * @return 登录结果
     */
    @Override
    public UserLoginResDTO loginByUserName(UserNameLoginReqDTO reqDTO) {
        String userName = reqDTO.getUsername();
        User user = accountMapper.findByUsername(userName);
        if (ObjectUtil.isEmpty(user)) {
            throw new CommonException(RespCode.DATA_NOT_EXIST, "用户不存在");
        }
        if (!EncoderUtils.matches(reqDTO.getPassword(), user.getPassword())) {
            throw new CommonException(RespCode.SYSTEM_ERROR, "密码错误");
        }
        // TODO 生成token
        return BeanUtil.copyProperties(user, UserLoginResDTO.class);
    }

    private void createUserToken(User user) {
    }
}
