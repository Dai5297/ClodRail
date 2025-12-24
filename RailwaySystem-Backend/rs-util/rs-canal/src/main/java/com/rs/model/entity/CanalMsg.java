package com.rs.model.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CanalMsg {

    private String type;

    private List<Map<String, String>> data;

    private List<Map<String, String>> old;
}
