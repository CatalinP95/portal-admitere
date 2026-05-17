package com.campus.userservice.service;

import com.campus.userservice.dto.AuthResponse;
import com.campus.userservice.dto.LoginRequest;
import com.campus.userservice.dto.RegisterRequest;
import com.campus.userservice.model.RefreshToken;
import com.campus.userservice.model.Role;
import com.campus.userservice.model.User;
import com.campus.userservice.repository.RefreshTokenRepository;
import com.campus.userservice.repository.UserRepository;
import com.campus.userservice.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.expiration:86400000}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpirationMs;

    private static final long REMEMBER_ME_ACCESS_MS  = 7L  * 24 * 3600 * 1000;
    private static final long REMEMBER_ME_REFRESH_MS = 30L * 24 * 3600 * 1000;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
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
        String rawRefreshToken = jwtUtil.generateRefreshToken(String.valueOf(saved.getId()), saved.getRole().name());

        saveRefreshToken(saved, rawRefreshToken);

        return new AuthResponse(accessToken, rawRefreshToken, saved.getUsername(), saved.getRole().name(), saved.getId());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        log.info("User logged in: {} (rememberMe={})", user.getUsername(), request.isRememberMe());

        refreshTokenRepository.revokeAllByUser(user);

        String accessToken;
        String rawRefreshToken;
        long refreshExpiry;

        if (request.isRememberMe()) {
            accessToken    = jwtUtil.generateTokenWithExpiry(String.valueOf(user.getId()), user.getRole().name(), REMEMBER_ME_ACCESS_MS);
            rawRefreshToken = jwtUtil.generateTokenWithExpiry(String.valueOf(user.getId()), user.getRole().name(), REMEMBER_ME_REFRESH_MS);
            refreshExpiry  = REMEMBER_ME_REFRESH_MS;
        } else {
            accessToken    = jwtUtil.generateToken(String.valueOf(user.getId()), user.getRole().name());
            rawRefreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()), user.getRole().name());
            refreshExpiry  = refreshExpirationMs;
        }

        saveRefreshToken(user, rawRefreshToken, refreshExpiry);

        return new AuthResponse(accessToken, rawRefreshToken, user.getUsername(), user.getRole().name(), user.getId());
    }

    @Transactional
    public AuthResponse refresh(String token) {
        RefreshToken stored = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (stored.isRevoked()) {
            throw new IllegalArgumentException("Refresh token revoked");
        }
        if (stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            stored.setRevoked(true);
            refreshTokenRepository.save(stored);
            throw new IllegalArgumentException("Refresh token expired");
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        User user = stored.getUser();
        String newAccessToken = jwtUtil.generateToken(String.valueOf(user.getId()), user.getRole().name());
        String newRawRefreshToken = jwtUtil.generateRefreshToken(String.valueOf(user.getId()), user.getRole().name());

        saveRefreshToken(user, newRawRefreshToken);

        return new AuthResponse(newAccessToken, newRawRefreshToken, user.getUsername(), user.getRole().name(), user.getId());
    }

    @Transactional
    public void logout(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(t -> {
            t.setRevoked(true);
            refreshTokenRepository.save(t);
            log.info("Logout: revoked token for user {}", t.getUser().getUsername());
        });
    }

    private void saveRefreshToken(User user, String rawToken) {
        saveRefreshToken(user, rawToken, refreshExpirationMs);
    }

    private void saveRefreshToken(User user, String rawToken, long expiryMs) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(rawToken);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(expiryMs / 1000));
        refreshTokenRepository.save(refreshToken);
    }
}
