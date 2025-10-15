package com.rs.mapper;

import com.rs.model.order.TicketOrderFailedMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FailedTicketOrderMsgMapper {

    void saveFailedMsg(TicketOrderFailedMessage ticketOrderFailedMessage);
}
