package com.rs.model;

import lombok.Data;

import java.util.List;

@Data
public class Group {

    private String groupName;

    private List<String> matchPath;
}
