package com.chenyq.domain;

import lombok.Data;

import java.util.List;

@Data
public class Terms {
    private Integer page;
    private Integer size;
    private String name;
    private String realname;
    private String cellphone;
    private Long createUser;
    private Integer status;
    // private String startTime;
    // private String endTime;
    private List<String>  createTime;
    private String intro;
    private String leader;
}
