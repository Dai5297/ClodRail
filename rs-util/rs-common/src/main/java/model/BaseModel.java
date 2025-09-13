package model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseModel {

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
