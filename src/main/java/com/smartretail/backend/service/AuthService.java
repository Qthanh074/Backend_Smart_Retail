package com.smartretail.backend.service;

import com.smartretail.backend.dto.request.RegisterRequest;
import com.smartretail.backend.entity.Role;
import com.smartretail.backend.entity.User;
import com.smartretail.backend.enums.RoleName;
import com.smartretail.backend.repository.RoleRepository;
import com.smartretail.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại trong hệ thống");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        // Mã hóa mật khẩu trước khi lưu vào DB
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Gán Role mặc định là STAFF cho người dùng tự đăng ký
        Role userRole = roleRepository.findByName(RoleName.ROLE_STAFF)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền mặc định"));

        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);
    }
}