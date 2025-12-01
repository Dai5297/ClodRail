package com.rs.client.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service", contextId = "userClient", path = "/inner/user")
public interface UserClient {

    @PostMapping("/username/list")
    Map<Long, String> usernameList(@RequestBody List<Long> userIds);
}
