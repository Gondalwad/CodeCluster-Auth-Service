package com.codecluster.auth.service.impl;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.codecluster.auth.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void blacklistToken(String token, long expiry) {

        redisTemplate.opsForValue().set(
                token,
                "BLACKLISTED",
                Duration.ofMillis(expiry)
        );

    }

    @Override
    public boolean isBlacklisted(String token) {

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(token)
        );

    }

}