package com.smartretail.backend.service;

import com.smartretail.backend.dto.request.RegisterRequest;
import com.smartretail.backend.entity.Role;
import com.smartretail.backend.entity.User;
import com.smartretail.backend.enums.RoleName;
import com.smartretail.backend.repository.RoleRepository;
import com.smartretail.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (request.getConfirmPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng trong hệ thống!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setEnabled(false);
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        Role userRole = roleRepository.findByName(RoleName.ROLE_STAFF)
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy quyền mặc định (ROLE_STAFF)"));
        user.setRoles(Collections.singleton(userRole));

        User savedUser = userRepository.save(user);

        try {
            emailService.sendRegistrationEmail(user.getEmail(), user.getFullName(), token);
        } catch (Exception e) {
            System.err.println("Không thể gửi email xác thực: " + e.getMessage());
        }

        return savedUser;
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Mã xác thực không hợp lệ hoặc đã hết hạn."));

        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }

}