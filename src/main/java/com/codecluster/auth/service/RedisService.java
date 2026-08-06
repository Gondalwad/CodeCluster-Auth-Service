package com.codecluster.auth.service;

public interface RedisService {

    void blacklistToken(String token, long expiry);

    boolean isBlacklisted(String token);

}