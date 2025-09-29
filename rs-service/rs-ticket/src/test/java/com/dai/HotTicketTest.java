package com.dai;

import com.dai.mapper.TicketMapper;
import com.dai.model.dto.response.HotTicketResDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class HotTicketTest {

    @Autowired
    private TicketMapper ticketMapper;

    @Test
    public void testHotTicket() {
        List<HotTicketResDTO> hotTicketResDTO = ticketMapper.queryHotTicket();
        System.out.println(hotTicketResDTO);
    }
}
