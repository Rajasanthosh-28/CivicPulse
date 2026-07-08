package com.civicpulse.controller;

import com.civicpulse.model.Admin;
import com.civicpulse.model.Complaint;
import com.civicpulse.model.Officer;
import com.civicpulse.service.AdminService;
import com.civicpulse.service.ComplaintService;
import com.civicpulse.service.OfficerService;
import com.civicpulse.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final OfficerService officerService;
    private final ComplaintService complaintService;

    public AdminController(AdminService adminService, UserService userService, OfficerService officerService, ComplaintService complaintService) {
        this.adminService = adminService;
        this.userService = userService;
        this.officerService = officerService;
        this.complaintService = complaintService;
    }

    @PostMapping("/admin-login")
    public String adminLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        var adminOptional = adminService.authenticate(email, password);
        if (adminOptional.isPresent()) {
            session.setAttribute("admin", adminOptional.get());
            return "redirect:/admin-dashboard";
        }
        model.addAttribute("errorMessage", "Invalid admin credentials");
        return "login";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/login";
        }
        model.addAttribute("admin", admin);
        model.addAttribute("citizens", userService.count());
        model.addAttribute("officers", officerService.count());
        model.addAttribute("complaints", complaintService.getAllComplaints());
        model.addAttribute("pending", complaintService.countByStatus("Pending"));
        model.addAttribute("assigned", complaintService.countByStatus("Assigned"));
        model.addAttribute("inProgress", complaintService.countByStatus("In Progress"));
        model.addAttribute("resolved", complaintService.countByStatus("Resolved"));
        model.addAttribute("closed", complaintService.countByStatus("Closed"));
        return "admin-dashboard";
    }

    @PostMapping("/officers")
    public String createOfficer(@RequestParam String fullName, @RequestParam String email, @RequestParam String password, @RequestParam String department, @RequestParam String phone) {
        Officer officer = new Officer();
        officer.setFullName(fullName);
        officer.setEmail(email);
        officer.setPassword(password);
        officer.setDepartment(department);
        officer.setPhone(phone);
        officerService.save(officer);
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/complaints/{id}/assign")
    public String assignComplaint(@PathVariable Long id, @RequestParam Long officerId) {
        Officer officer = officerService.findById(officerId).orElse(null);
        if (officer != null) {
            complaintService.assignOfficer(id, officer);
        }
        return "redirect:/admin-dashboard";
    }

    @PostMapping("/complaints/{id}/delete")
    public String deleteComplaint(@PathVariable Long id) {
        complaintService.deleteById(id);
        return "redirect:/admin-dashboard";
    }
}
