package com.campus.userservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableCaching
@EnableAsync
public class CacheConfiguration implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("Cache GET error on '{}' key={}: {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("Cache PUT error on '{}' key={}: {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("Cache EVICT error on '{}' key={}: {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("Cache CLEAR error on '{}': {}", cache.getName(), e.getMessage());
            }
        };
    }
}
