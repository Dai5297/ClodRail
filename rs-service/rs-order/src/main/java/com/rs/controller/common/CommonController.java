package com.rs.controller.common;

import com.rs.dto.request.ticket.AssistantOrderMsgDTO;
import com.rs.service.CommonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "公共订单接口接口")
@RequestMapping("/common/orders")
public class CommonController {

    private final CommonService commonService;

    @PostMapping("/assistant/generate/{id}")
    public String generateAssistantOrder(@PathVariable String id) {
        return commonService.generateAssistantOrder(id);
    }

    @GetMapping("/assistant/prase")
    public AssistantOrderMsgDTO praseAssistantOrder(@RequestParam String uuid) {
        return commonService.praseAssistantOrder(uuid);
    }
}
