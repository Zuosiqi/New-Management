package com.attendance.attendanceservice.service;

import com.attendance.attendanceservice.entity.LeaveRequest;

import java.util.List;

/**
 * 请假服务接口
 */
public interface LeaveRequestService {

    /**
     * 提交请假申请
     */
    LeaveRequest submitLeaveRequest(LeaveRequest leaveRequest);

    /**
     * 根据ID获取请假申请
     */
    LeaveRequest getLeaveRequestById(Long id);

    /**
     * 获取员工的请假申请列表
     */
    List<LeaveRequest> getLeaveRequestsByEmployeeId(Long employeeId);

    /**
     * 获取待审批的请假申请
     */
    List<LeaveRequest> getPendingLeaveRequests();

    /**
     * 审批请假申请
     */
    LeaveRequest approveLeaveRequest(Long id, Long approverId, String approverName, Integer status, String remark);
}
