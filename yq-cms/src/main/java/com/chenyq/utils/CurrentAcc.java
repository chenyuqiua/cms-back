package com.chenyq.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chenyq.domain.Token;
import com.chenyq.service.TokenService;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class CurrentAcc {
    /**
     * 封装方法获取当前用户id
     * @param request
     * @return
     */
    public static Long getCurrentId(HttpServletRequest request, TokenService tokenService) {
        String token = (String) request.getSession().getAttribute("token");
        LambdaQueryWrapper<Token> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Token::getToken, token);
        Token tokenAcc = tokenService.getOne(lqw);
        Long id = tokenAcc.getAccountId();
        return id;
    }
}
