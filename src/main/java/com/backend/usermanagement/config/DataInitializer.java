package com.backend.usermanagement.config;

import com.backend.usermanagement.domain.entity.Role;
import com.backend.usermanagement.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.findByName("USER").isEmpty()){
            roleRepository.save(new Role("USER"));
        }
        if (roleRepository.findByName("ADMIN").isEmpty()){
            roleRepository.save(new Role("ADMIN"));
        }
    }
}
