package com.attendance.leaveservice.controller;

import com.attendance.leaveservice.entity.LeaveRequest;
import com.attendance.leaveservice.feign.EmployeeFeignClient;
import com.attendance.leaveservice.service.LeaveRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leave")
public class LeaveController {

    private final LeaveRequestService leaveRequestService;
    private final EmployeeFeignClient employeeFeignClient;

    public LeaveController(LeaveRequestService leaveRequestService,
                           EmployeeFeignClient employeeFeignClient) {
        this.leaveRequestService = leaveRequestService;
        this.employeeFeignClient = employeeFeignClient;
    }

    @GetMapping("/test")
    public String test() {
        return "leave-service is running";
    }

    /**
     * Feign 调用演示：获取员工信息
     */
    @GetMapping("/employee/{employeeId}")
    public Map<String, Object> getEmployeeInfo(@PathVariable Long employeeId) {
        return employeeFeignClient.getEmployeeById(employeeId);
    }

    /**
     * Feign 调用测试接口
     */
    @GetMapping("/feign-test")
    public String feignTest() {
        return employeeFeignClient.test();
    }

    @PostMapping("/apply")
    public LeaveRequest applyLeave(@RequestBody LeaveRequest leaveRequest) {
        return leaveRequestService.createLeaveRequest(leaveRequest);
    }

    @GetMapping("/list")
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestService.getAllLeaveRequests();
    }

    @GetMapping("/{id}")
    public LeaveRequest getLeaveRequestById(@PathVariable Long id) {
        return leaveRequestService.getLeaveRequestById(id);
    }

    @GetMapping("/employee/{employeeId}")
    public List<LeaveRequest> getLeaveRequestsByEmployeeId(@PathVariable Long employeeId) {
        return leaveRequestService.getLeaveRequestsByEmployeeId(employeeId);
    }

    @GetMapping("/status/{status}")
    public List<LeaveRequest> getLeaveRequestsByStatus(@PathVariable String status) {
        return leaveRequestService.getLeaveRequestsByStatus(status);
    }

    @PutMapping("/{id}/approve")
    public LeaveRequest approveLeaveRequest(@PathVariable Long id) {
        return leaveRequestService.approveLeaveRequest(id);
    }

    @PutMapping("/{id}/reject")
    public LeaveRequest rejectLeaveRequest(@PathVariable Long id) {
        return leaveRequestService.rejectLeaveRequest(id);
    }
}