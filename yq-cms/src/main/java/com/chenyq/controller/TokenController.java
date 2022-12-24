package com.chenyq.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chenyq.domain.Account;
import com.chenyq.service.TokenService;
import com.chenyq.utils.Result;
import com.chenyq.domain.Token;
import com.chenyq.service.AccountService;
import com.chenyq.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/login")
public class TokenController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private TokenService tokenService;

    /**
     * 用户登录: 判断账号密码是否正确, 并返回token
     * @param request
     * @param account
     * @return
     */
    @PostMapping()
    public Result<Token> login(HttpServletRequest request, @RequestBody Account account) {
        // 将页面提交的密码进行md5加密
        String password = account.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.error(password);

        // 根据页面提交的用户名和密码查询数据库
        LambdaQueryWrapper<Account> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Account::getName, account.getName());
        Account acc = accountService.getOne(lqw);

        // 校验账号密码是否正确, 账号是否禁用
        if (acc == null) {
            return Result.error("您输入的用户名不存在, 请重新输入!");
        }
        if (!acc.getPassword().equals(password)) {
            log.error(acc.getPassword());
            log.error(account.getPassword());
            return Result.error("您输入的密码不正确, 请重新输入!");
        }
        if (acc.getStatus() == 0) {
            return Result.error("您的账号已禁用, 请联系管理员");
        }

        // 封装对象用于新增或修改到数据库中
        Token accountToken = new Token(); // 存储到数据库中的对象
        accountToken.setId(acc.getId());
        accountToken.setName(account.getName());
        String token = TokenUtils.generateToken();
        accountToken.setToken(token);
        accountToken.setAccountId(acc.getId());
        SimpleDateFormat formatdate = new SimpleDateFormat("yyyy-MM-dd :HH:mm:ss");
        accountToken.setCreateTime(formatdate.format(new Date()));
        accountToken.setUpdateTime(formatdate.format(new Date()));
        accountToken.setCreateUser(acc.getId());
        accountToken.setUpdateUser(acc.getId());

        // 判断数据库是否存在该用户名的记录
        LambdaQueryWrapper<Token> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(Token::getName, account.getName());
        Token acct = tokenService.getOne(lqw1);
        if (acct != null) {
            tokenService.updateById(accountToken);
        } else {
            tokenService.save(accountToken);
        }

        // 重新从数据库获取对象返回
        Token acct1 = tokenService.getOne(lqw1);
        // token存到session中
        request.getSession().setAttribute("token", acct1.getToken());
        return Result.success(acct1);
    }
}
