package com.attendance.attendanceservice.repository;

import com.attendance.attendanceservice.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 考勤记录数据访问层
 */
@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    /**
     * 根据员工ID和日期查找考勤记录
     */
    Optional<AttendanceRecord> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    /**
     * 根据员工ID查找考勤记录
     */
    List<AttendanceRecord> findByEmployeeId(Long employeeId);

    /**
     * 根据日期查找考勤记录
     */
    List<AttendanceRecord> findByDate(LocalDate date);

    /**
     * 根据员工ID和日期范围查找考勤记录
     */
    List<AttendanceRecord> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
}
