package com.rs.controller.user;

import com.rs.model.PageResult;
import com.rs.model.dto.request.ContactReqDTO;
import com.rs.model.dto.response.ContactAddResDTO;
import com.rs.model.dto.response.ContactDetailResDTO;
import com.rs.model.dto.response.ContactPageResDTO;
import com.rs.service.ContactService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "联系人管理")
@RequiredArgsConstructor
@RequestMapping("/contact")
public class ContactController {

    private final ContactService contactService;

    @GetMapping("/page")
    public PageResult<ContactPageResDTO> page(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "passengerType", required = false) Integer passengerType,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        return contactService.page(page, size, name, passengerType, status);
    }

    @GetMapping("/{id}")
    public ContactDetailResDTO detail(@PathVariable Long id) {
        return contactService.detail(id);
    }

    @PostMapping("/add")
    public ContactAddResDTO add(@RequestBody ContactReqDTO contactAddReqDTO) {
        return contactService.add(contactAddReqDTO);
    }

    @PutMapping("/update")
    public ContactDetailResDTO update(@RequestBody ContactReqDTO contactUpdateReqDTO) {
        return contactService.update(contactUpdateReqDTO);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        contactService.delete(id);
    }
}
