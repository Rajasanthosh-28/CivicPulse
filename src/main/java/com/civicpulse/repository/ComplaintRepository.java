package com.civicpulse.repository;

import com.civicpulse.model.Complaint;
import com.civicpulse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByCitizen(User citizen);
    List<Complaint> findByStatus(String status);
    List<Complaint> findByCategory(String category);

    long countByStatus(String status);
    long countByCitizen(User citizen);
    List<Complaint> findByCitizenOrderByCreatedAtDesc(User citizen);
    Optional<Complaint> findByComplaintUid(String complaintUid);
}
