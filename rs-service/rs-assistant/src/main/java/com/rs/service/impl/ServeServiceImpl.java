package com.rs.service.impl;

import com.rs.manage.AssistantManage;
import com.rs.service.ServeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServeServiceImpl implements ServeService {

    private final AssistantManage assistantManage;

    @Override
    public Integer status() {
        return assistantManage.countOnlineAgent();
    }
}
