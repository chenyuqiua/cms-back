package com.chenyq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenyq.mapper.TokenMapper;
import com.chenyq.domain.Token;
import com.chenyq.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl extends ServiceImpl<TokenMapper, Token> implements TokenService {
}
