package com.transport.tms.service;

import com.transport.tms.domain.entity.Role;
import com.transport.tms.domain.entity.User;
import com.transport.tms.dto.request.UserRequest;
import com.transport.tms.dto.response.PageResponse;
import com.transport.tms.dto.response.RoleResponse;
import com.transport.tms.dto.response.UserResponse;
import com.transport.tms.exception.BusinessException;
import com.transport.tms.exception.ResourceNotFoundException;
import com.transport.tms.repository.RoleRepository;
import com.transport.tms.repository.UserRepository;
import com.transport.tms.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> list(int page, int size) {
        return PageMapper.map(
                userRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size)),
                this::toResponse
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("DUPLICATE_USERNAME", "Ce nom d'utilisateur est déjà pris");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new BusinessException("PASSWORD_REQUIRED", "Un mot de passe est requis pour la création");
        }
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .active(request.active())
                .driverId(request.driverId())
                .roles(resolveRoles(request.roleCodes()))
                .build();
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = findById(id);
        // Check username uniqueness only if changed
        if (!user.getUsername().equals(request.username()) && userRepository.existsByUsername(request.username())) {
            throw new BusinessException("DUPLICATE_USERNAME", "Ce nom d'utilisateur est déjà pris");
        }
        user.setUsername(request.username());
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setActive(request.active());
        user.setDriverId(request.driverId());
        user.setRoles(resolveRoles(request.roleCodes()));
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        return roleRepository.findAll().stream()
                .map(r -> new RoleResponse(r.getId(), r.getCode(), r.getLabel()))
                .toList();
    }

    // ─── Private helpers ───────────────────────────────────────────────────────

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private Set<Role> resolveRoles(List<String> codes) {
        if (codes == null || codes.isEmpty()) return new HashSet<>();
        return new HashSet<>(roleRepository.findByCodeIn(codes));
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getUsername(),
                u.getFullName(),
                u.getEmail(),
                u.getPhone(),
                u.isActive(),
                u.getDriverId(),
                u.getCreatedAt(),
                u.getRoles().stream().map(Role::getCode).sorted().toList()
        );
    }
}
