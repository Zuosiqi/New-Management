package com.attendance.attendanceservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假申请实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "leave_request")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "employee_no", nullable = false, length = 20)
    private String employeeNo;

    @Column(name = "employee_name", nullable = false, length = 50)
    private String employeeName;

    @Column(name = "leave_type", nullable = false, length = 20)
    private String leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer days;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer status = 0;

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approver_name", length = 50)
    private String approverName;

    @Column(name = "approve_time")
    private LocalDateTime approveTime;

    @Column(name = "approve_remark", length = 200)
    private String approveRemark;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
