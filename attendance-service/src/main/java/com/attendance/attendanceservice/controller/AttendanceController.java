package com.attendance.attendanceservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AttendanceController {

    @GetMapping("/attendance/test")
    public String test() {
        return "attendance-service is running";
    }
}