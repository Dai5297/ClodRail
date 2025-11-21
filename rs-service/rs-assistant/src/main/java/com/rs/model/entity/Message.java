package com.rs.model.entity;

import lombok.Data;

@Data
public class Message {

    private String type;

    private String content;

    private String from;

    private String to;

}
