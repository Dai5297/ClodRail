package com.rs.model.mall;

import com.rs.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PointBalance extends BaseModel {

    private Long id;

    private Long userId;

    private Long currentPoints;

    private Long totalEarned;

    private Long totalSpent;
}
