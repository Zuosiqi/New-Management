package com.attendance.attendanceservice.service.impl;

import com.attendance.attendanceservice.entity.LeaveRequest;
import com.attendance.attendanceservice.feign.EmployeeFeignClient;
import com.attendance.attendanceservice.repository.LeaveRequestRepository;
import com.attendance.attendanceservice.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 请假服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeFeignClient employeeFeignClient;

    @Override
    @Transactional
    public LeaveRequest submitLeaveRequest(LeaveRequest leaveRequest) {
        // 通过 Feign 调用获取员工信息
        Map employeeInfo = getEmployeeInfo(leaveRequest.getEmployeeId());
        leaveRequest.setEmployeeNo((String) employeeInfo.get("employeeNo"));
        leaveRequest.setEmployeeName((String) employeeInfo.get("name"));

        // 计算请假天数
        long days = java.time.temporal.ChronoUnit.DAYS.between(
                leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;
        leaveRequest.setDays((int) days);

        leaveRequest.setStatus(0); // 待审批
        log.info("员工 {} 提交请假申请，类型: {}，天数: {}", leaveRequest.getEmployeeName(), leaveRequest.getLeaveType(), days);

        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    public LeaveRequest getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("请假申请不存在，ID: " + id));
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByEmployeeId(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<LeaveRequest> getPendingLeaveRequests() {
        return leaveRequestRepository.findByStatus(0);
    }

    @Override
    @Transactional
    public LeaveRequest approveLeaveRequest(Long id, Long approverId, String approverName, Integer status, String remark) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        if (leaveRequest.getStatus() != 0) {
            throw new RuntimeException("该请假申请已审批，无法重复审批");
        }

        leaveRequest.setStatus(status);
        leaveRequest.setApproverId(approverId);
        leaveRequest.setApproverName(approverName);
        leaveRequest.setApproveTime(LocalDateTime.now());
        leaveRequest.setApproveRemark(remark);

        log.info("请假申请 {} 已审批，状态: {}", id, status == 1 ? "批准" : "拒绝");

        return leaveRequestRepository.save(leaveRequest);
    }

    /**
     * 通过 Feign 调用获取员工信息
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getEmployeeInfo(Long employeeId) {
        var result = employeeFeignClient.getEmployeeById(employeeId);
        if (result.getCode() != 200) {
            throw new RuntimeException("获取员工信息失败: " + result.getMessage());
        }
        return (Map<String, Object>) result.getData();
    }
}
