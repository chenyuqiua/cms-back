package com.chenyq.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
@TableName("account_role")
public class Role {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private String intro;
    @TableField(exist = false)
    private List<Long> menuList; // 用于接收权限id集合
    @TableField(exist = false)
    private List<Menu> menus; // 用于返回权限信息
    private String createTime;
    private String updateTime;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUser;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long updateUser;

    @JsonIgnore
    public List<Long> getMenuList() {
        return menuList;
    }

    @JsonProperty
    public void setMenuList(List<Long> menuList) {
        this.menuList = menuList;
    }
}
