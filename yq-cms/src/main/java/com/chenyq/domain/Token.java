package com.chenyq.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("account_token")
public class Token {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private String token;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long accountId;
    private String createTime;
    private String updateTime;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUser;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUser;
}
