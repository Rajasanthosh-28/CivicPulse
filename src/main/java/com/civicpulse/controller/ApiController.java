package com.civicpulse.controller;

import com.civicpulse.model.User;
import com.civicpulse.service.ComplaintService;
import com.civicpulse.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ComplaintService complaintService;
    private final NotificationService notificationService;

    public ApiController(ComplaintService complaintService, NotificationService notificationService) {
        this.complaintService = complaintService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> dashboardStats(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).build();
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", complaintService.countByCitizenUser(user));
        stats.put("pending", complaintService.countByStatus("Pending"));
        stats.put("inProgress", complaintService.countByStatus("In Progress"));
        stats.put("resolved", complaintService.countByStatus("Resolved"));
        stats.put("assigned", complaintService.countByStatus("Assigned"));
        stats.put("closed", complaintService.countByStatus("Closed"));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/notifications/count")
    public ResponseEntity<Map<String, Long>> notificationCount(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user.getId())));
    }
}
