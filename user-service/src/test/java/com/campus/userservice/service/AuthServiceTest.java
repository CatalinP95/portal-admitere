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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
        when(jwtUtil.generateTokenWithExpiry(eq("1"), eq("STUDENT"), anyLong())).thenReturn("access", "refresh");
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
}
