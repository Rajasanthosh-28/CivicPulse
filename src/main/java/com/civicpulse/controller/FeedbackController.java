package com.civicpulse.controller;

import com.civicpulse.service.FeedbackService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/feedback-list")
    public String feedbackList(Model model) {
        model.addAttribute("feedbackList", feedbackService.getAllFeedback());
        return "feedback";
    }
}
