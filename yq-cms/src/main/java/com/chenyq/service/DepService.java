package com.chenyq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chenyq.domain.Dep;
import com.chenyq.domain.ResultList;
import com.chenyq.domain.Terms;
import com.chenyq.utils.Result;

public interface DepService extends IService<Dep> {
    Result<ResultList> selectPageByDomain(Terms terms);
}
