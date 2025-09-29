package com.dai.mapper;

import com.dai.model.dto.request.SearchTicketReqDTO;
import com.dai.model.dto.response.HotTicketResDTO;
import com.dai.model.dto.response.SearchTicketResDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TicketMapper {

    /**
     * 查询最热门的10条车票
     */
    List<HotTicketResDTO> queryHotTicket();

    /**
     * 搜索车票
     */
    List<SearchTicketResDTO> searchTicket(SearchTicketReqDTO reqDTO);
}
