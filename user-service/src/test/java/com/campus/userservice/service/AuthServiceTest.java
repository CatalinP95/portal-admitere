package com.campus.userservice.service;

import com.campus.userservice.dto.AuthResponse;
import com.campus.userservice.dto.LoginRequest;
import com.campus.userservice.dto.RegisterRequest;
import com.campus.userservice.model.Role;
import com.campus.userservice.model.User;
import com.campus.userservice.model.RefreshToken;
import com.campus.userservice.repository.RefreshTokenRepository;
import com.campus.userservice.repository.UserRepository;
import com.campus.userservice.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("ion"); req.setEmail("ion@test.com"); req.setPassword("pass123");

        when(userRepository.existsByUsername("ion")).thenReturn(false);
        when(userRepository.existsByEmail("ion@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");

        User saved = new User();
        saved.setId(1L); saved.setUsername("ion"); saved.setRole(Role.STUDENT);
        when(userRepository.save(any())).thenReturn(saved);
        when(jwtUtil.generateToken("1", "STUDENT")).thenReturn("access");
        when(jwtUtil.generateRefreshToken("1", "STUDENT")).thenReturn("refresh");
        when(refreshTokenRepository.save(any())).thenReturn(new RefreshToken());

        AuthResponse response = authService.register(req);

        assertNotNull(response);
        assertEquals("access", response.getAccessToken());
        assertEquals("STUDENT", response.getRole());
    }

    @Test
    void register_duplicateUsername_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("existing"); req.setEmail("new@test.com"); req.setPassword("pass123");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_duplicateEmail_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("new"); req.setEmail("exists@test.com"); req.setPassword("pass123");

        when(userRepository.existsByUsername("new")).thenReturn(false);
        when(userRepository.existsByEmail("exists@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(req));
    }

    @Test
    void login_success() {
        LoginRequest req = new LoginRequest();
        req.setUsername("ion"); req.setPassword("pass123");

        User user = new User();
        user.setId(1L); user.setUsername("ion"); user.setRole(Role.STUDENT);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("ion")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("1", "STUDENT")).thenReturn("access");
        when(jwtUtil.generateRefreshToken("1", "STUDENT")).thenReturn("refresh");
        doNothing().when(refreshTokenRepository).revokeAllByUser(any());
        when(refreshTokenRepository.save(any())).thenReturn(new RefreshToken());

        AuthResponse response = authService.login(req);

        assertNotNull(response);
        assertEquals("STUDENT", response.getRole());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void login_badCredentials_throws() {
        LoginRequest req = new LoginRequest();
        req.setUsername("ion"); req.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(req));
    }

    @Test
    void refresh_success() {
        User user = new User();
        user.setId(1L); user.setUsername("ion"); user.setRole(Role.STUDENT);

        RefreshToken stored = new RefreshToken();
        stored.setToken("refresh-token");
        stored.setRevoked(false);
        stored.setExpiryDate(LocalDateTime.now().plusDays(7));
        stored.setUser(user);

        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(stored));
        when(jwtUtil.generateToken("1", "STUDENT")).thenReturn("new-access");
        when(jwtUtil.generateRefreshToken("1", "STUDENT")).thenReturn("new-refresh");
        when(refreshTokenRepository.save(any())).thenReturn(new RefreshToken());

        AuthResponse response = authService.refresh("refresh-token");

        assertEquals("new-access", response.getAccessToken());
        assertEquals("STUDENT", response.getRole());
        assertTrue(stored.isRevoked());
    }

    @Test
    void refresh_revokedToken_throws() {
        RefreshToken stored = new RefreshToken();
        stored.setToken("revoked-token");
        stored.setRevoked(true);
        stored.setExpiryDate(LocalDateTime.now().plusDays(1));

        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(stored));

        assertThrows(IllegalArgumentException.class, () -> authService.refresh("revoked-token"));
    }

    @Test
    void refresh_expiredToken_throws() {
        RefreshToken stored = new RefreshToken();
        stored.setToken("expired-token");
        stored.setRevoked(false);
        stored.setExpiryDate(LocalDateTime.now().minusDays(1));

        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(stored));
        when(refreshTokenRepository.save(any())).thenReturn(stored);

        assertThrows(IllegalArgumentException.class, () -> authService.refresh("expired-token"));
        assertTrue(stored.isRevoked());
    }

    @Test
    void refresh_tokenNotFound_throws() {
        when(refreshTokenRepository.findByToken("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.refresh("unknown"));
    }

    @Test
    void logout_revokesToken() {
        User user = new User();
        user.setId(1L); user.setUsername("ion"); user.setRole(Role.STUDENT);

        RefreshToken stored = new RefreshToken();
        stored.setToken("my-token");
        stored.setRevoked(false);
        stored.setUser(user);

        when(refreshTokenRepository.findByToken("my-token")).thenReturn(Optional.of(stored));
        when(refreshTokenRepository.save(any())).thenReturn(stored);

        authService.logout("my-token");

        assertTrue(stored.isRevoked());
        verify(refreshTokenRepository).save(stored);
    }

    @Test
    void logout_unknownToken_doesNothing() {
        when(refreshTokenRepository.findByToken("ghost")).thenReturn(Optional.empty());

        authService.logout("ghost");

        verify(refreshTokenRepository, never()).save(any());
    }
}
