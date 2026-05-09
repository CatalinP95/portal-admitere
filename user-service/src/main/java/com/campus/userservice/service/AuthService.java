package com.campus.userservice.service;

import com.campus.userservice.dto.AuthResponse;
import com.campus.userservice.dto.LoginRequest;
import com.campus.userservice.dto.RegisterRequest;
import com.campus.userservice.model.Role;
import com.campus.userservice.model.User;
import com.campus.userservice.repository.UserRepository;
import com.campus.userservice.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
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
        user.setEnabled(true);

        User saved = userRepository.save(user);
        log.info("Registered new user: {}", saved.getUsername());

        String accessToken = jwtUtil.generateToken(String.valueOf(saved.getId()), saved.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(String.valueOf(saved.getId()), saved.getRole().name());

        return new AuthResponse(accessToken, refreshToken, saved.getRole().name(), saved.getId());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        log.info("User logged in: {}", user.getUsername());

        String accessToken = jwtUtil.generateToken(String.valueOf(user.getId()), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()), user.getRole().name());

        return new AuthResponse(accessToken, refreshToken, user.getRole().name(), user.getId());
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String userId = jwtUtil.extractUserId(refreshToken);
        String role = jwtUtil.extractRole(refreshToken);

        String newAccessToken = jwtUtil.generateToken(userId, role);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, role);

        return new AuthResponse(newAccessToken, newRefreshToken, role, Long.parseLong(userId));
    }
}
