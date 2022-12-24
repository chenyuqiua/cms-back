package com.chenyq.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("account_role_menu")
public class RoleMenu {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long roleId;
    private Long menuId;
    private String createTime;
    private String updateTime;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUser;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUser;
}
