package com.rs.service;

import com.rs.dto.request.ticket.AssistantOrderMsgDTO;

public interface CommonService {

    /**
     * 生成订单短链接
     *
     * @return 链接
     */
    String generateAssistantOrder(String id);

    /**
     * 解析订单短链接
     *
     * @param uuid 链接
     * @return 订单信息
     */
    AssistantOrderMsgDTO praseAssistantOrder(String uuid);
}
