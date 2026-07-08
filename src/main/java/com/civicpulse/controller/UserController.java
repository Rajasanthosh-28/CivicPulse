package com.civicpulse.controller;

import com.civicpulse.model.User;
import com.civicpulse.service.ComplaintService;
import com.civicpulse.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;
    private final ComplaintService complaintService;

    public UserController(UserService userService, ComplaintService complaintService) {
        this.userService = userService;
        this.complaintService = complaintService;
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Email already registered");
            return "register";
        }
        userService.registerUser(user);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        var userOptional = userService.authenticate(email, password);
        if (userOptional.isPresent()) {
            session.setAttribute("user", userOptional.get());
            return "redirect:/citizen-dashboard";
        }
        model.addAttribute("errorMessage", "Invalid credentials");
        return "login";
    }

    @GetMapping("/citizen-dashboard")
    public String citizenDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        // Add dashboard stats
        model.addAttribute("totalComplaints", complaintService.countByCitizenUser(user));
        model.addAttribute("pendingCount", complaintService.countByStatus("Pending"));
        model.addAttribute("inProgressCount", complaintService.countByStatus("In Progress"));
        model.addAttribute("resolvedCount", complaintService.countByStatus("Resolved"));
        model.addAttribute("assignedCount", complaintService.countByStatus("Assigned"));
        model.addAttribute("recentComplaints", complaintService.getComplaintsByCitizenSorted(user));
        return "citizen-dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", userService.getUserById(user.getId()));
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User user, HttpSession session) {
        User current = (User) session.getAttribute("user");
        if (current == null) {
            return "redirect:/login";
        }
        current.setFullName(user.getFullName());
        current.setPhone(user.getPhone());
        current.setAddress(user.getAddress());
        userService.save(current);
        session.setAttribute("user", current);
        return "redirect:/profile";
    }

    @GetMapping("/settings")
    public String settingsPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "settings";
    }

    @GetMapping("/help")
    public String helpPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "help";
    }
}

