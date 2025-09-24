package com.dai.model.domain;

import com.dai.annotation.CreateBy;
import com.dai.annotation.CreateTime;
import com.dai.annotation.UpdateBy;
import com.dai.annotation.UpdateTime;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseModel {

    @CreateBy
    private Long createBy;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateBy
    private Long updateBy;

    @UpdateTime
    private LocalDateTime updateTime;
}
