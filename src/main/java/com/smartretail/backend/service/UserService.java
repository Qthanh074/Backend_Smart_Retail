package com.smartretail.backend.service;

import com.smartretail.backend.entity.User;
import com.smartretail.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại!");
        }
        // Lưu ý: Trong thực tế cần mã hóa password trước khi lưu
        return userRepository.save(user);
    }
}