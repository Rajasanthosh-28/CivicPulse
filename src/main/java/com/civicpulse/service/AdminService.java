package com.civicpulse.service;

import com.civicpulse.model.Admin;
import com.civicpulse.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Optional<Admin> authenticate(String email, String password) {
        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isPresent() && adminOptional.get().getPassword().equals(password)) {
            return adminOptional;
        }
        return Optional.empty();
    }

    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }
}
