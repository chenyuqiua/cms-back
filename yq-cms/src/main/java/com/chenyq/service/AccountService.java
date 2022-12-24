package com.chenyq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chenyq.domain.ResultList;
import com.chenyq.domain.Account;
import com.chenyq.domain.Terms;
import com.chenyq.utils.Result;

public interface AccountService extends IService<Account> {
    Result<ResultList> selectPageByDomain(Terms terms, RoleService roleService, DepService depService);
}
