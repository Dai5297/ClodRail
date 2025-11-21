package com.rs.controller.inner;

import com.rs.dto.response.customer.PassengerResDTO;
import com.rs.service.ContactService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "内部联系人接口")
@RequestMapping("/inner/contact")
public class innerContactController {

    private final ContactService contactService;

    @GetMapping("/query/passenger")
    List<PassengerResDTO> queryPassenger(@RequestParam List<Long> passengerIds) {
        return contactService.queryPassenger(passengerIds);
    }
}
