package com.rs.controller.inner;

import com.rs.service.TickerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "内部接口-票务相关接口")
@RequestMapping("/inner/tickets")
public class InnerTicketController {

    private final TickerService tickerService;
}
