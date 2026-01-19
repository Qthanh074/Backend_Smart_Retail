package com.smartretail.backend.repository;

import com.smartretail.backend.entity.Role;
import com.smartretail.backend.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}