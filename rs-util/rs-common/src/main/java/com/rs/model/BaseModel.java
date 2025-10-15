package com.rs.model;

import com.rs.annotation.CreateBy;
import com.rs.annotation.CreateTime;
import com.rs.annotation.UpdateBy;
import com.rs.annotation.UpdateTime;
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
