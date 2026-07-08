package com.civicpulse.controller;

import com.civicpulse.model.User;
import com.civicpulse.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public String notificationsPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("notifications", notificationService.getNotifications(user.getId()));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user.getId()));
        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllAsRead(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) notificationService.markAllAsRead(user.getId());
        return "redirect:/notifications";
    }
}
