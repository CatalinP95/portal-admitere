package com.campus.userservice.service;

import com.campus.userservice.dto.RegisterRequest;
import com.campus.userservice.dto.UserDto;
import com.campus.userservice.exception.UserNotFoundException;
import com.campus.userservice.model.Role;
import com.campus.userservice.model.User;
import com.campus.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserDto::from);
    }

    public Page<UserDto> findFiltered(String search, String roleStr, Pageable pageable) {
        String searchParam = (search == null || search.isBlank()) ? null : search;
        Role roleParam = (roleStr == null || roleStr.isBlank()) ? null : Role.valueOf(roleStr.toUpperCase());
        return userRepository.findWithFilters(searchParam, roleParam, pageable).map(UserDto::from);
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(UserDto::from)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public UserDto create(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);

        User saved = userRepository.save(user);
        log.info("Created user: {}", saved.getUsername());
        return UserDto.from(saved);
    }

    public UserDto update(Long id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        log.info("Updated user: {}", user.getUsername());
        return UserDto.from(userRepository.save(user));
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setEnabled(false);
        userRepository.save(user);
        log.info("Disabled user: {}", user.getUsername());
    }

    public UserDto changeRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setRole(Role.valueOf(role.toUpperCase()));
        log.info("Changed role for user {} to {}", user.getUsername(), role);
        return UserDto.from(userRepository.save(user));
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Parola curentă este incorectă");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", user.getUsername());
    }
}
