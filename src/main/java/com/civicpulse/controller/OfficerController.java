package com.civicpulse.controller;

import com.civicpulse.model.Officer;
import com.civicpulse.service.ComplaintService;
import com.civicpulse.service.OfficerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OfficerController {

    private final OfficerService officerService;
    private final ComplaintService complaintService;

    public OfficerController(OfficerService officerService, ComplaintService complaintService) {
        this.officerService = officerService;
        this.complaintService = complaintService;
    }

    @PostMapping("/officer-login")
    public String officerLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        var officerOptional = officerService.authenticate(email, password);
        if (officerOptional.isPresent()) {
            session.setAttribute("officer", officerOptional.get());
            return "redirect:/officer-dashboard";
        }
        model.addAttribute("errorMessage", "Invalid officer credentials");
        return "login";
    }

    @GetMapping("/officer-dashboard")
    public String officerDashboard(HttpSession session, Model model) {
        Officer officer = (Officer) session.getAttribute("officer");
        if (officer == null) {
            return "redirect:/login";
        }
        model.addAttribute("complaints", complaintService.getAllComplaints());
        model.addAttribute("officer", officer);
        return "officer-dashboard";
    }

    @PostMapping("/complaints/{id}/status")
    public String updateComplaintStatus(@PathVariable Long id, @RequestParam String status) {
        complaintService.updateStatus(id, status);
        return "redirect:/officer-dashboard";
    }

    @PostMapping("/complaints/{id}/remarks")
    public String addRemarks(@PathVariable Long id, @RequestParam String remarks) {
        complaintService.addRemarks(id, remarks);
        return "redirect:/officer-dashboard";
    }
}
