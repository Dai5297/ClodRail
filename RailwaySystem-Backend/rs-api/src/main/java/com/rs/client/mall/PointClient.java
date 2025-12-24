package com.rs.client.mall;

import com.rs.dto.request.mall.AddPointReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mall-service", contextId = "pointClient", path = "/inner/point")
public interface PointClient {

    @PostMapping("/addPoint")
    void addPoint(@RequestBody AddPointReqDTO pointReqDTO);
}
