package com.campus.dormitory.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserClientService {

    private final ObjectProvider<UserClient> userClient;

    public UserClientService(ObjectProvider<UserClient> userClient) {
        this.userClient = userClient;
    }

    @CircuitBreaker(name = "user-service", fallbackMethod = "getUserFallback")
    @Retry(name = "user-service")
    public Optional<UserDto> getUser(Long userId) {
        UserClient client = userClient.getIfAvailable();
        if (client == null) {
            return Optional.empty();
        }
        UserDto user = client.getUserById(userId);
        return Optional.ofNullable(user);
    }

    @SuppressWarnings("unused")
    private Optional<UserDto> getUserFallback(Long userId, Throwable t) {
        log.warn("user-service call failed for userId={}, returning empty fallback. Cause: {}",
                userId, t.getMessage());
        return Optional.empty();
    }
}
