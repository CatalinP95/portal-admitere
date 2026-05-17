package com.campus.userservice.service;

import com.campus.userservice.dto.UserProfileDto;
import com.campus.userservice.dto.UserProfileRequest;
import com.campus.userservice.model.User;
import com.campus.userservice.model.UserProfile;
import com.campus.userservice.repository.UserProfileRepository;
import com.campus.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public UserProfileService(UserProfileRepository userProfileRepository,
                               UserRepository userRepository,
                               AuditLogService auditLogService) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @CacheEvict(value = "profiles", key = "#userId")
    public void evictCache(Long userId) {
        log.info("Cache evicted for user {}", userId);
    }

    public Page<UserProfileDto> getAll(Pageable pageable) {
        return userProfileRepository.findAll(pageable).map(UserProfileDto::from);
    }

    @Cacheable(value = "profiles", key = "#userId")
    public UserProfileDto getByUserId(Long userId) {
        log.info("Cache MISS — loading profile from DB for user {}", userId);
        auditLogService.log(userId, "READ", "UserProfile", "Loaded profile for user " + userId);
        return userProfileRepository.findByUserId(userId)
                .map(UserProfileDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user: " + userId));
    }

    @CacheEvict(value = "profiles", key = "#userId")
    public UserProfileDto save(Long userId, UserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (request.getCnp() != null && !request.getCnp().isBlank()) {
            boolean cnpTaken = userProfileRepository.existsByCnp(request.getCnp());
            boolean ownProfile = userProfileRepository.findByUserId(userId)
                    .map(p -> request.getCnp().equals(p.getCnp()))
                    .orElse(false);
            if (cnpTaken && !ownProfile) {
                throw new IllegalArgumentException("CNP already registered");
            }
        }

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile());
        profile.setUser(user);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setCnp(request.getCnp());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setPhone(request.getPhone());

        UserProfile saved = userProfileRepository.save(profile);
        log.info("Saved profile for user: {}", userId);
        auditLogService.log(userId, "WRITE", "UserProfile", "Updated profile for user " + userId);
        return UserProfileDto.from(saved);
    }
}
