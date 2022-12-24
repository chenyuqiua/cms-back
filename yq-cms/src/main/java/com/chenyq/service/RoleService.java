package com.chenyq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chenyq.domain.ResultList;
import com.chenyq.domain.Role;
import com.chenyq.domain.Terms;
import com.chenyq.utils.Result;


public interface RoleService extends IService<Role> {
    Result<ResultList> selectPageByDomain(Terms terms);
}
