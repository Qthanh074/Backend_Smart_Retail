package com.smartretail.backend.config;

import com.smartretail.backend.entity.Role;
import com.smartretail.backend.entity.User;
import com.smartretail.backend.enums.RoleName;
import com.smartretail.backend.repository.RoleRepository;
import com.smartretail.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }

        // 2. Khởi tạo tài khoản Demo
        createDemoUser("super@example.com", RoleName.ROLE_SUPER_ADMIN);
        createDemoUser("admin@example.com", RoleName.ROLE_ADMIN);
        createDemoUser("manager@example.com", RoleName.ROLE_MANAGER);
        createDemoUser("staff@example.com", RoleName.ROLE_STAFF);
    }

    private void createDemoUser(String email, RoleName roleName) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("Default123!"));
            roleRepository.findByName(roleName).ifPresent(role -> {
                user.setRoles(Collections.singleton(role));
                userRepository.save(user);
            });
        }
    }
}