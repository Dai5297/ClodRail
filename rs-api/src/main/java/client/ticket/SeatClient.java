package client.ticket;

import dto.request.FetchSeatReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ticket-service", contextId = "seatClient", path = "/inner/seats")
public interface SeatClient {

    /**
     * 获取座位
     *
     * @param reqDTO 请求参数
     * @return 座位id
     */
    @PostMapping("/fetch")
    Long fetchSeat(@RequestBody FetchSeatReqDTO reqDTO);
}
