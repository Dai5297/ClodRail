package com.rs;

import com.rs.client.ticket.TicketClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestClient {

    @Autowired
    private TicketClient ticketClient;

    @Test
    public void test() {
        System.out.println(ticketClient.list(List.of(1L)));
    }
}
