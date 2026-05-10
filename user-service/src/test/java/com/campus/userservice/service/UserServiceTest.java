package com.campus.userservice.service;

import com.campus.userservice.dto.RegisterRequest;
import com.campus.userservice.dto.UserDto;
import com.campus.userservice.model.Role;
import com.campus.userservice.model.User;
import com.campus.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User buildUser(Long id, String username, Role role) {
        User u = new User();
        u.setId(id); u.setUsername(username);
        u.setEmail(username + "@test.com");
        u.setRole(role); u.setEnabled(true);
        return u;
    }

    @Test
    void findAll_returnsPage() {
        Page<User> page = new PageImpl<>(List.of(buildUser(1L, "ion", Role.STUDENT)));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<UserDto> result = userService.findAll(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals("ion", result.getContent().get(0).getUsername());
    }

    @Test
    void findById_found_returnsDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L, "ion", Role.STUDENT)));

        UserDto dto = userService.findById(1L);

        assertEquals("ion", dto.getUsername());
    }

    @Test
    void findById_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findById(99L));
    }

    @Test
    void create_success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("nou"); req.setEmail("nou@test.com"); req.setPassword("pass123");

        when(userRepository.existsByUsername("nou")).thenReturn(false);
        when(userRepository.existsByEmail("nou@test.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(buildUser(1L, "nou", Role.STUDENT));

        UserDto result = userService.create(req);

        assertEquals("nou", result.getUsername());
        verify(userRepository).save(any());
    }

    @Test
    void create_duplicateUsername_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("exists"); req.setEmail("x@x.com"); req.setPassword("pass123");

        when(userRepository.existsByUsername("exists")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.create(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_disablesUser() {
        User user = buildUser(1L, "ion", Role.STUDENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        userService.delete(1L);

        assertFalse(user.isEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void changeRole_updatesRole() {
        User user = buildUser(1L, "ion", Role.STUDENT);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.changeRole(1L, "ADMIN");

        assertEquals(Role.ADMIN, user.getRole());
    }
}
