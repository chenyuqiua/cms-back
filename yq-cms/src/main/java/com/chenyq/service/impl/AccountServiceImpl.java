package com.chenyq.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenyq.domain.*;
import com.chenyq.mapper.AccountMapper;
import com.chenyq.service.AccountService;
import com.chenyq.service.DepService;
import com.chenyq.service.RoleService;
import com.chenyq.utils.FormatDate;
import com.chenyq.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Override
    public Result<ResultList> selectPageByDomain(Terms terms, RoleService roleService, DepService depService) {
        log.error(String.valueOf(terms));
        terms.getCreateTime();
        log.error("terms: " + terms);
        // 定义查询条件
        LambdaQueryWrapper<Account> lqw = new LambdaQueryWrapper<>();
        if (terms.getName() != null) {
            lqw.like(Account::getName, terms.getName());
        }
        if (terms.getRealname() != null) {
            lqw.like(Account::getRealname, terms.getRealname());
        }
        if (terms.getCellphone() != null) {
            lqw.like(Account::getCellphone, terms.getCellphone());
        }
        if (terms.getCreateUser() != null) {
            lqw.like(Account::getCreateUser, terms.getCreateUser());
        }
        if (terms.getStatus() != null) {
            lqw.eq(Account::getStatus, terms.getStatus());
        }

        if (terms.getCreateTime() != null && terms.getCreateTime().size() != 0) {
            String statTime = FormatDate.formatDate(terms.getCreateTime().get(0));
            String endTime = FormatDate.formatDate(terms.getCreateTime().get(1));
            log.error(statTime);
            log.error(endTime);
            lqw.between(Account::getCreateTime, statTime, endTime);
        }
        lqw.orderByDesc(Account::getCreateTime);

        if (terms.getPage() != null && terms.getSize() != null) {
            // 按条件分页查询 获取满足条件的用户集合
            IPage page = new Page(terms.getPage(), terms.getSize());
            this.page(page, lqw);
            List<Account> accountList = page.getRecords();

            // 封装role和dep添加到结果中返回
            for (Account account: accountList) {
                // 查询权限信息
                Role role = roleService.getById(account.getRoleId());
                account.setRole(role);
                // 查询部门信息
                Dep dep = depService.getById(account.getDepId());
                account.setDep(dep);
            }

            // 封装返回结果的其他信息
            ResultList resultList = new ResultList<Account>();
            resultList.setList(accountList);
            resultList.setCurrentPage(page.getCurrent());
            resultList.setSize(page.getSize());
            resultList.setTotalPages(page.getPages());
            resultList.setTotalCount(page.getTotal());

            return Result.success(resultList);
        } else {
            // 按条件查询全部
            List<Account> accounts = this.list(lqw);
            ResultList resultList = new ResultList();
            resultList.setList(accounts);

            return Result.success(resultList);
        }
    }
}
