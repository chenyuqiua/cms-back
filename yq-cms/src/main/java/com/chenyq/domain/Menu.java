package com.chenyq.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
@TableName("account_menu")
public class Menu {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private Integer type;
    private String url;
    private String icon;
    private Integer sort;
    private String createTime;
    private String updateTime;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUser;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUser;

    // 数据库不存在的属性
    @TableField(exist = false)
    private List<SubMenu> children;
}
