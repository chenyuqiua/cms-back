package com.chenyq.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chenyq.domain.*;
import com.chenyq.mapper.AccountMapper;
import com.chenyq.service.DepService;
import com.chenyq.service.RoleService;
import com.chenyq.service.TokenService;
import com.chenyq.service.impl.AccountServiceImpl;
import com.chenyq.utils.CurrentAcc;
import com.chenyq.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/users")
public class AccountController {
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private RoleService roleService;
    @Autowired
    private DepService depService;
    @Autowired
    private TokenService tokenService;
    private SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd :HH:mm:ss");

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Account> selectById(@PathVariable BigInteger id) {
        LambdaQueryWrapper<Account> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Account::getId, id);
        Account account = accountService.getOne(lqw);

        if (account == null) {
            return Result.error("您查询的的用户不存在!");
        }

        // 获取权限信息添加到结果中返回
        Role role = roleService.getById(account.getRoleId());
        if (role != null) {
            account.setRole(role);
        }

        // 获取部门信息添加到结果中返回
        Dep dep = depService.getById(account.getDepId());
        if (dep != null) {
            account.setDep(dep);
        }

        return Result.success(account);
    }

    /**
     * 按条件查询全部或按条件分页查询
     * @param terms
     * @return
     */
    @PostMapping("/list")
    public Result<ResultList> selectAll(@RequestBody Terms terms) {
        return accountService.selectPageByDomain(terms, roleService, depService);
    }

    /**
     * 新增用户接口
     * @param request
     * @param account
     * @return
     */
    @PostMapping()
    public Result<String> save(HttpServletRequest request, @RequestBody Account account) {
        if (account.getName() == null ||
                account.getPassword() == null ||
                account.getRealname() == null ||
                account.getCellphone() == null ||
                account.getRoleId() == null ||
                account.getDepId() == null
        ) {
            return Result.error("请输入完整的注册信息!");
        }

        // 设置密码进行md5加密
        account.setPassword(DigestUtils.md5DigestAsHex(account.getPassword().getBytes()));

        // 设置创建修改时间 状态
        account.setCreateTime(formatdate.format(new Date()));
        account.setUpdateTime(formatdate.format(new Date()));
        account.setStatus(1);

        // 设置创建修改人
        Long id = CurrentAcc.getCurrentId(request, tokenService);
        account.setCreateUser(id);
        account.setUpdateUser(id);

        accountService.save(account);
        return Result.success("新增成功");
    }

    /**
     * 修改/更新用户功能接口
     * @param request
     * @param account
     * @return
     */
    @PutMapping()
    public Result<String> update(HttpServletRequest request, @RequestBody Account account) {
        log.error(String.valueOf(account));
        // 设置更新时间更新人
        account.setUpdateTime(formatdate.format(new Date()));
        Long currentId = CurrentAcc.getCurrentId(request, tokenService);
        account.setUpdateUser(currentId);

        boolean res = accountService.updateById(account);
        if (res) {
            return Result.success("修改成功!");
        }
        return Result.error("修改失败!");
    }

    /**
     * 删除用户功能接口
     */
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = {NullPointerException.class})
    public Result<String> delete(@PathVariable Long id) {
        if (id <= 10) {
            return Result.error("id小于10的数据不允许删除!");
        }

        LambdaQueryWrapper<Token> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Token::getAccountId, id);
        tokenService.remove(lqw);

        boolean remAcc = accountService.removeById(id);

        if (remAcc) {
            return Result.success("删除成功!");
        }
        return Result.error("删除失败!");
    }
}
