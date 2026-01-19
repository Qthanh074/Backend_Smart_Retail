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

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // Thêm EmailService

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) { // Khởi tạo EmailService
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void register(RegisterRequest request) {
        // 1. Kiểm tra xác nhận mật khẩu
        if (request.getConfirmPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp!");
        }

        // 2. Kiểm tra email đã tồn tại hay chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng trong hệ thống!");
        }

        // 3. Tạo đối tượng User và mapping thông tin từ Request
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());

        // 4. Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Mặc định tài khoản được kích hoạt khi đăng ký
        user.setEnabled(true);

        // 5. Gán Role mặc định là ROLE_STAFF cho người dùng tự đăng ký
        Role userRole = roleRepository.findByName(RoleName.ROLE_STAFF)
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy quyền mặc định (ROLE_STAFF)"));

        user.setRoles(Collections.singleton(userRole));

        // 6. Lưu xuống database
        userRepository.save(user);

        // 7. Tích hợp gửi Email sau khi đăng ký thành công
        try {
            emailService.sendRegistrationEmail(user.getEmail(), user.getFullName());
        } catch (Exception e) {
            // Log lỗi nhưng không làm thất bại quá trình đăng ký chính
            System.err.println("Không thể gửi email thông báo: " + e.getMessage());
        }
    }
}