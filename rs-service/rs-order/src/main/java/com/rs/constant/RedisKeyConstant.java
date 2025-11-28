package com.rs.constant;

public class RedisKeyConstant {

    public static final String ICR_TICKET_ORDER = "icr:ticket:order:";

    public static final String TICKET_USER_TIME = "ticket:user:time:";

    public static final String TICKET_ORDER = "ticket:order:";

    public static final String TICKET_ORDER_INFO = "ticket:order:info:";

    public static final Long TICKET_ORDER_INFO_TTL = 60 * 60 * 24L;

    public static final String TICKET_ORDER_ID = "ticket:order:id:";

    public static final Long TICKET_ORDER_ID_TTL = 60 * 60 * 24L;

    public static final String TICKET_SEAT = "ticket:seat:";

    public static final Long TICKET_SEAT_TTL = 60 * 60 * 24L;

    public static final String TICKET_DEDUCTION_TAG = "ticket:deduction:tag";

    public static final Long TICKET_DEDUCTION_TAG_TTL = 60 * 60 * 24L;

    public static final String TICKET_HOT = "ticket:hot:";

    public static final String TICKET_STORE = "ticket:store:";

    public static final String ASSISTANT_ORDER = "assistant:order:";
}
