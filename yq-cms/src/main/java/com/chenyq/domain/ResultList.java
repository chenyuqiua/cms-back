package com.chenyq.domain;

import lombok.Data;

import java.util.List;

@Data
public class ResultList<T> {
    private List<T> list;
    private Long currentPage;
    private Long size;
    private Long totalPages;
    private Long totalCount;
}
