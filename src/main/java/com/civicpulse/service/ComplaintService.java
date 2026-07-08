package com.civicpulse.service;

import com.civicpulse.model.Complaint;
import com.civicpulse.model.Officer;
import com.civicpulse.model.User;
import com.civicpulse.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public Complaint save(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public List<Complaint> getComplaintsByCitizen(User citizen) {
        return complaintRepository.findByCitizen(citizen);
    }

    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    public Complaint assignOfficer(Long complaintId, Officer officer) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow();
        complaint.setAssignedOfficer(officer);
        complaint.setStatus("Assigned");
        return complaintRepository.save(complaint);
    }

    public Complaint updateStatus(Long complaintId, String status) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow();
        complaint.setStatus(status);
        return complaintRepository.save(complaint);
    }

    public Complaint addRemarks(Long complaintId, String remarks) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow();
        complaint.setRemarks(remarks);
        return complaintRepository.save(complaint);
    }

    public void deleteById(Long id) {
        complaintRepository.deleteById(id);
    }

    public long count() {
        return complaintRepository.count();
    }

    public long countByStatus(String status) {
        return complaintRepository.countByStatus(status);
    }

    public String generateComplaintUid() {
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = (int)(Math.random() * 9000) + 1000;
        return "CP-" + datePart + "-" + rand;
    }

    public long countByCitizenUser(User citizen) {
        return complaintRepository.countByCitizen(citizen);
    }

    public Optional<Complaint> getComplaintByUid(String uid) {
        return complaintRepository.findByComplaintUid(uid);
    }

    public List<Complaint> getComplaintsByCitizenSorted(User citizen) {
        return complaintRepository.findByCitizenOrderByCreatedAtDesc(citizen);
    }
}

