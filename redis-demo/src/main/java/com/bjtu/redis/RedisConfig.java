package com.bjtu.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    @Bean("RedisCounter")
    public RedisCounter configCounter(){
        return new RedisCounter();
    }

    @Bean("JSONConfig")
    public JSONConfig JSONConfig(){
        return new JSONConfig();
    }
}