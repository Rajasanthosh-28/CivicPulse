package com.civicpulse.service;

import com.civicpulse.model.Officer;
import com.civicpulse.repository.OfficerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OfficerService {

    private final OfficerRepository officerRepository;
    private final PasswordEncoder passwordEncoder;

    public OfficerService(OfficerRepository officerRepository, PasswordEncoder passwordEncoder) {
        this.officerRepository = officerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Officer save(Officer officer) {
        officer.setPassword(passwordEncoder.encode(officer.getPassword()));
        return officerRepository.save(officer);
    }

    public Optional<Officer> authenticate(String email, String password) {
        Optional<Officer> officerOptional = officerRepository.findByEmail(email);
        if (officerOptional.isPresent()) {
            Officer officer = officerOptional.get();
            if (passwordEncoder.matches(password, officer.getPassword())) {
                return Optional.of(officer);
            }
        }
        return Optional.empty();
    }

    public Optional<Officer> findById(Long id) {
        return officerRepository.findById(id);
    }

    public long count() {
        return officerRepository.count();
    }
}
