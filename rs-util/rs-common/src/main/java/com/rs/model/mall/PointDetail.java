package com.rs.model.mall;

import com.rs.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PointDetail extends BaseModel {
    private Long id;

    private Long userId;

    private Integer type;

    private Integer point;

    private String comment;
}
