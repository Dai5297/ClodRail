package com.rs.constant;

import com.rs.model.customer.User;
import io.netty.util.AttributeKey;

public class AttributeKeyConstant {

    public static final AttributeKey<String> SESSION_ID = AttributeKey.valueOf("sessionId");

    public static final AttributeKey<User> USER = AttributeKey.valueOf("user");

    // TODO 改为Admin类
    public static final AttributeKey<Object> AGENT = AttributeKey.valueOf("agent");
}
