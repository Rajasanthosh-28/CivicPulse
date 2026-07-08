package com.civicpulse.config;

import com.civicpulse.model.*;
import com.civicpulse.repository.*;
import com.civicpulse.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final OfficerService officerService;
    private final AdminService adminService;
    private final ComplaintService complaintService;
    private final NotificationService notificationService;

    public DataInitializer(UserService userService, OfficerService officerService, AdminService adminService,
                           ComplaintService complaintService, NotificationService notificationService) {
        this.userService = userService;
        this.officerService = officerService;
        this.adminService = adminService;
        this.complaintService = complaintService;
        this.notificationService = notificationService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userService.count() == 0) {
            // 1. Seed Citizen
            User citizen = new User();
            citizen.setFullName("Rahul Sharma");
            citizen.setEmail("citizen@example.com");
            citizen.setPassword("password123");
            citizen.setPhone("+91 98765 43210");
            citizen.setAddress("42 Mahatma Gandhi Road, Sector 12, New Delhi - 110001");
            userService.registerUser(citizen);

            // 2. Seed Officer
            Officer officer = new Officer();
            officer.setFullName("Vikram Singh");
            officer.setEmail("officer@example.com");
            officer.setPassword("password123");
            officer.setDepartment("Roads & Infrastructure");
            officer.setPhone("+91 98111 22333");
            officerService.save(officer);

            // 3. Seed Admin
            Admin admin = new Admin();
            admin.setFullName("System Commissioner");
            admin.setEmail("admin@example.com");
            admin.setPassword("password123");
            adminService.save(admin);

            // 4. Seed Sample Grievances
            Complaint c1 = new Complaint();
            c1.setTitle("Severe Pothole on Sector 12 Main Arterial Road");
            c1.setDescription("Deep pothole spanning across two lanes near the metro station causing heavy traffic jams and danger to two-wheelers.");
            c1.setCategory("Roads");
            c1.setPriority("HIGH");
            c1.setLocation("Opposite Sector 12 Metro Station Gate #2");
            c1.setStatus("Pending");
            c1.setComplaintUid("CP-20260708-1001");
            c1.setCitizen(citizen);
            complaintService.save(c1);

            Complaint c2 = new Complaint();
            c2.setTitle("Irregular Water Supply & Low Pipeline Pressure");
            c2.setDescription("Residents in Block B are facing erratic municipal water timings with extremely low water pressure during morning hours.");
            c2.setCategory("Water Supply");
            c2.setPriority("MEDIUM");
            c2.setLocation("Block B Residential Society, Sector 12");
            c2.setStatus("Assigned");
            c2.setComplaintUid("CP-20260708-1002");
            c2.setCitizen(citizen);
            c2.setAssignedOfficer(officer);
            complaintService.save(c2);

            Complaint c3 = new Complaint();
            c3.setTitle("Street Lights Non-Functional Along Park Avenue");
            c3.setDescription("Over 6 consecutive street light poles along the public jogging park are not functioning for the last 4 days, creating safety issues at night.");
            c3.setCategory("Street Lights");
            c3.setPriority("URGENT");
            c3.setLocation("Park Avenue Walking Trail, Sector 12");
            c3.setStatus("In Progress");
            c3.setComplaintUid("CP-20260708-1003");
            c3.setRemarks("Maintenance crew dispatched to check underground cable faults.");
            c3.setCitizen(citizen);
            c3.setAssignedOfficer(officer);
            complaintService.save(c3);

            Complaint c4 = new Complaint();
            c4.setTitle("Overflowing Garbage Dumpster Near Community Hall");
            c4.setDescription("Community dumpster has not been cleared by collection trucks for over 48 hours leading to unsanitary conditions.");
            c4.setCategory("Garbage Collection");
            c4.setPriority("MEDIUM");
            c4.setLocation("Behind Community Hall, Sector 12");
            c4.setStatus("Resolved");
            c4.setComplaintUid("CP-20260708-1004");
            c4.setRemarks("Garbage cleared completely and sanitization powder sprayed on site by sanitation division.");
            c4.setCitizen(citizen);
            c4.setAssignedOfficer(officer);
            complaintService.save(c4);

            // 5. Seed Notifications for Citizen
            notificationService.createNotification(citizen.getId(),
                    "Grievance Resolved",
                    "Your grievance 'Overflowing Garbage Dumpster Near Community Hall' (ID: CP-20260708-1004) has been marked Resolved by Officer Vikram Singh.",
                    "COMPLAINT_UPDATE", "/complaints/" + c4.getId());

            notificationService.createNotification(citizen.getId(),
                    "Officer Assigned to Grievance",
                    "Officer Vikram Singh (Roads & Infrastructure) has been assigned to your grievance 'Street Lights Non-Functional Along Park Avenue' (ID: CP-20260708-1003).",
                    "COMPLAINT_UPDATE", "/complaints/" + c3.getId());

            notificationService.createNotification(citizen.getId(),
                    "Welcome to CivicPulse",
                    "Welcome to the upgraded Smart Citizen Complaint Management Portal. Explore our new dark mode, live status tracking, and SLA alerts.",
                    "SYSTEM", "/help");
        }
    }
}
