package com.attendance.attendanceservice.repository;

import com.attendance.attendanceservice.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 请假申请数据访问层
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    /**
     * 根据员工ID查找请假申请
     */
    List<LeaveRequest> findByEmployeeId(Long employeeId);

    /**
     * 根据状态查找请假申请
     */
    List<LeaveRequest> findByStatus(Integer status);

    /**
     * 根据审批人ID查找请假申请
     */
    List<LeaveRequest> findByApproverId(Long approverId);
}
