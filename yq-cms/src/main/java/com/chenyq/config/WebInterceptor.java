package com.chenyq.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chenyq.domain.Token;
import com.chenyq.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class WebInterceptor implements HandlerInterceptor {
    @Autowired
    private TokenService tokenService;

    /**
     * preHandle 表示方法请求前的处理，若其返回值为FALSE，就中断请求目标方法了，只有返回值为TRUE时才继续执行方法内容
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String authorization = httpServletRequest.getHeader("Authorization");
        String token = authorization.split(" ")[1];
        log.debug("token: " +token);
        LambdaQueryWrapper<Token> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Token::getToken, token);
        Token acct = tokenService.getOne(lqw);

        //token验证
        if(acct == null){
            System.out.println("token验证失败");
            return false;
        }
        // token存到session中
        httpServletRequest.getSession().setAttribute("token", token);
        System.out.println("token验证成功");
        return true;
    }

    /**
     * postHandle 是在目标方法执行完之后执行的
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println(" WebInterceptor | postHandle ================");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println(" WebInterceptor | afterCompletion  ================");
    }
}
