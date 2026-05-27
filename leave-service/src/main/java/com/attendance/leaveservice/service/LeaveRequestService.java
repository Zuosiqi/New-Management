package com.attendance.leaveservice.service;

import com.attendance.leaveservice.entity.LeaveRequest;
import com.attendance.leaveservice.feign.EmployeeFeignClient;
import com.attendance.leaveservice.repository.LeaveRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeFeignClient employeeFeignClient;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository,
                               EmployeeFeignClient employeeFeignClient) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeFeignClient = employeeFeignClient;
    }

    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest) {
        leaveRequest.setId(null);
        leaveRequest.setStatus("PENDING");
        leaveRequest.setCreateTime(LocalDateTime.now());

        // 通过 Feign 调用获取员工信息
        try {
            Map<String, Object> employeeInfo = employeeFeignClient.getEmployeeById(leaveRequest.getEmployeeId());
            String employeeName = (String) employeeInfo.get("name");
            String employeeNo = (String) employeeInfo.get("employeeNo");
            log.info("获取员工信息成功：{} - {}", employeeNo, employeeName);
        } catch (Exception e) {
            log.warn("获取员工信息失败，使用默认值: {}", e.getMessage());
        }

        return leaveRequestRepository.save(leaveRequest);
    }

    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    public LeaveRequest getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));
    }

    public List<LeaveRequest> getLeaveRequestsByEmployeeId(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    public List<LeaveRequest> getLeaveRequestsByStatus(String status) {
        return leaveRequestRepository.findByStatus(status);
    }

    public LeaveRequest approveLeaveRequest(Long id) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);
        leaveRequest.setStatus("APPROVED");
        return leaveRequestRepository.save(leaveRequest);
    }

    public LeaveRequest rejectLeaveRequest(Long id) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);
        leaveRequest.setStatus("REJECTED");
        return leaveRequestRepository.save(leaveRequest);
    }
}