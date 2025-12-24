package com.rs.controller.admin;

import com.rs.model.dto.response.AdminMemoryResDTO;
import com.rs.service.AdminMemoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "客服回复历史接口")
@RequestMapping("/admin/memory")
public class MemoryController {

    private final AdminMemoryService memoryService;

    @GetMapping("/list")
    @Operation(summary = "获取所有会话列表")
    public List<AdminMemoryResDTO> memoryList() {
        return memoryService.memoryList();
    }

    @GetMapping("/detail/{sessionId}")
    @Operation(summary = "获取会话详情")
    public AdminMemoryResDTO getMemoryDetail(@PathVariable String sessionId) {
        return memoryService.getMemoryDetail(sessionId);
    }
}
