package com.deliverywindow.deliveryservice.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

import static org.springframework.boot.actuate.autoconfigure.health.HealthEndpointProperties.*;

@Component
public class RedisCacheErrorHandler implements CacheErrorHandler {

    private static Logger logger =LoggerFactory.getLogger(RedisCacheErrorHandler.class.getName());

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        logger.warn("Redis GET failed for key {}. Falling back to source.", key);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        logger.warn("Redis PUT failed. Key: {}", key, exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        logger.warn("Redis EVICT failed. Key: {}", key, exception);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        logger.warn("Redis CLEAR failed.", exception);
    }
}
