package com.attendance.leaveservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaveController {

    @GetMapping("/leave/test")
    public String test() {
        return "leave-service is running";
    }
}