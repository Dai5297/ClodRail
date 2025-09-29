package com.dai.service;

import com.dai.model.PageResult;
import com.dai.model.dto.request.SearchTicketReqDTO;
import com.dai.model.dto.response.HotTicketResDTO;
import com.dai.model.dto.response.SearchTicketResDTO;

import java.util.List;

public interface TickerService {

     /**
     * 获取最热门的10条车票信息
     * @return 最热门的10条车票信息
     */
    List<HotTicketResDTO> hotTicket();

    /**
     * 搜索车票信息
     *
     * @param reqDTO 搜索车票信息请求参数
     * @return 搜索车票信息
     */
    PageResult<SearchTicketResDTO> searchTicket(SearchTicketReqDTO reqDTO);
}
