package com.transport.tms.controller;

import com.transport.tms.dto.request.UserRequest;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.dto.response.RoleResponse;
import com.transport.tms.dto.response.UserResponse;
import com.transport.tms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    // ─── Users ─────────────────────────────────────────────────────────────────

    @GetMapping("/api/v1/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public PageResponse<UserResponse> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return userService.list(page, size);
    }

    @GetMapping("/api/v1/users/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping("/api/v1/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    @PutMapping("/api/v1/users/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/api/v1/users/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }

    // ─── Roles ─────────────────────────────────────────────────────────────────

    @GetMapping("/api/v1/roles")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public List<RoleResponse> listRoles() {
        return userService.listRoles();
    }
}
