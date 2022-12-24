package com.chenyq.utils;

import java.util.Random;
import java.util.UUID;

public class TokenUtils {
    public static String generateToken(){
        //生成唯一不重复的字符串
        String token = System.currentTimeMillis() + new Random().nextInt(999999999) + UUID.randomUUID().toString();
        return token;
    }
}
