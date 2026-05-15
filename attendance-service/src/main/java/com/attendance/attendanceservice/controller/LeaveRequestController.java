package com.attendance.attendanceservice.controller;

import com.attendance.attendanceservice.dto.Result;
import com.attendance.attendanceservice.entity.LeaveRequest;
import com.attendance.attendanceservice.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 请假控制器
 */
@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * 提交请假申请
     */
    @PostMapping("/apply")
    public Result<LeaveRequest> applyLeave(@RequestBody LeaveRequest leaveRequest) {
        try {
            LeaveRequest created = leaveRequestService.submitLeaveRequest(leaveRequest);
            return Result.success("请假申请提交成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取请假申请
     */
    @GetMapping("/{id}")
    public Result<LeaveRequest> getLeaveRequestById(@PathVariable Long id) {
        try {
            LeaveRequest leaveRequest = leaveRequestService.getLeaveRequestById(id);
            return Result.success(leaveRequest);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取员工的请假申请列表
     */
    @GetMapping("/employee/{employeeId}")
    public Result<List<LeaveRequest>> getLeaveRequestsByEmployeeId(@PathVariable Long employeeId) {
        List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByEmployeeId(employeeId);
        return Result.success(leaveRequests);
    }

    /**
     * 获取待审批的请假申请
     */
    @GetMapping("/pending")
    public Result<List<LeaveRequest>> getPendingLeaveRequests() {
        List<LeaveRequest> leaveRequests = leaveRequestService.getPendingLeaveRequests();
        return Result.success(leaveRequests);
    }

    /**
     * 审批请假申请
     */
    @PostMapping("/{id}/approve")
    public Result<LeaveRequest> approveLeaveRequest(
            @PathVariable Long id,
            @RequestParam Long approverId,
            @RequestParam String approverName,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        try {
            LeaveRequest leaveRequest = leaveRequestService.approveLeaveRequest(id, approverId, approverName, status, remark);
            return Result.success("审批成功", leaveRequest);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
