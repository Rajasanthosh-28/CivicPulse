package com.civicpulse.controller;

import com.civicpulse.model.Complaint;
import com.civicpulse.model.Feedback;
import com.civicpulse.model.User;
import com.civicpulse.service.ComplaintService;
import com.civicpulse.service.FeedbackService;
import com.civicpulse.service.NotificationService;
import com.civicpulse.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Controller
public class ComplaintController {

    private final ComplaintService complaintService;
    private final FeedbackService feedbackService;
    private final NotificationService notificationService;
    private final UserService userService;

    public ComplaintController(ComplaintService complaintService, FeedbackService feedbackService,
                               NotificationService notificationService, UserService userService) {
        this.complaintService = complaintService;
        this.feedbackService = feedbackService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/complaint-form")
    public String complaintForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("complaint", new Complaint());
        return "complaint-form";
    }

    @PostMapping("/complaints")
    public String submitComplaint(@ModelAttribute Complaint complaint,
                                  @RequestParam(required = false) String priority,
                                  @RequestParam(required = false) String phone,
                                  @RequestParam(required = false) String address,
                                  @RequestParam(required = false) MultipartFile imageFile,
                                  HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // Update citizen contact details if provided
        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhone(phone.trim());
        }
        if (address != null && !address.trim().isEmpty()) {
            user.setAddress(address.trim());
        }
        userService.save(user);
        session.setAttribute("user", user);

        complaint.setCitizen(user);
        if (complaint.getComplaintUid() == null || complaint.getComplaintUid().isEmpty()) {
            complaint.setComplaintUid(complaintService.generateComplaintUid());
        }
        if (priority != null && !priority.isEmpty()) {
            complaint.setPriority(priority);
        }

        // Handle attachment upload if present
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                File uploadDir = new File("uploads");
                if (!uploadDir.exists()) uploadDir.mkdirs();
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                imageFile.transferTo(new File(uploadDir, fileName));
                complaint.setImagePath("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        complaintService.save(complaint);

        if (complaint.getCitizen() != null) {
            notificationService.createNotification(complaint.getCitizen().getId(), "Complaint Submitted", "Your complaint '" + complaint.getTitle() + "' has been submitted with tracking ID: " + complaint.getComplaintUid(), "COMPLAINT_UPDATE", "/complaints/" + complaint.getId());
        }

        return "redirect:/complaint-history";
    }

    @GetMapping("/complaint-history")
    public String complaintHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("complaints", complaintService.getComplaintsByCitizen(user));
        return "complaint-history";
    }

    @GetMapping("/complaints/{id}")
    public String complaintDetails(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        Complaint complaint = complaintService.getComplaintById(id).orElse(null);
        model.addAttribute("complaint", complaint);
        return "complaint-details";
    }

    @GetMapping("/feedback")
    public String feedbackPage(@RequestParam(required = false) Long complaintId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        model.addAttribute("feedback", new Feedback());
        model.addAttribute("complaintId", complaintId);
        return "feedback";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@ModelAttribute Feedback feedback, @RequestParam Long complaintId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        feedback.setCitizen(user);
        Complaint complaint = complaintService.getComplaintById(complaintId).orElse(null);
        feedback.setComplaint(complaint);
        feedbackService.save(feedback);
        return "redirect:/complaint-history";
    }

    @GetMapping("/track-complaint")
    public String trackComplaintPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "track-complaint";
    }

    @PostMapping("/track-complaint")
    public String trackComplaint(@RequestParam String searchQuery, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("searchQuery", searchQuery);
        var complaint = complaintService.getComplaintByUid(searchQuery);
        if (complaint.isPresent()) {
            model.addAttribute("foundComplaint", complaint.get());
        } else {
            model.addAttribute("notFound", true);
        }
        return "track-complaint";
    }
}
