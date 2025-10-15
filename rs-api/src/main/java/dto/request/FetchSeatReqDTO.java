package dto.request;

import lombok.Data;

@Data
public class FetchSeatReqDTO {

    private Long ticketId;

    private Integer seatType;

    private String seatPosition;
}
