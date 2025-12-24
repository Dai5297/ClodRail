package com.rs.constant;

import com.rs.model.customer.Admin;
import com.rs.model.customer.User;
import io.netty.util.AttributeKey;

public class AttributeKeyConstant {

    public static final AttributeKey<String> SESSION_ID = AttributeKey.valueOf("sessionId");

    public static final AttributeKey<User> USER = AttributeKey.valueOf("user");

    public static final AttributeKey<Admin> AGENT = AttributeKey.valueOf("agent");
}
