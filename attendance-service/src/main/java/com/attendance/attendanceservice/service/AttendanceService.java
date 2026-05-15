package com.attendance.attendanceservice.service;

import com.attendance.attendanceservice.entity.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤服务接口
 */
public interface AttendanceService {

    /**
     * 上班打卡
     */
    AttendanceRecord clockIn(Long employeeId);

    /**
     * 下班打卡
     */
    AttendanceRecord clockOut(Long employeeId);

    /**
     * 根据ID获取考勤记录
     */
    AttendanceRecord getRecordById(Long id);

    /**
     * 获取员工当日考勤记录
     */
    AttendanceRecord getTodayRecord(Long employeeId);

    /**
     * 获取员工考勤记录列表
     */
    List<AttendanceRecord> getRecordsByEmployeeId(Long employeeId);

    /**
     * 获取指定日期的考勤记录
     */
    List<AttendanceRecord> getRecordsByDate(LocalDate date);

    /**
     * 获取员工指定日期范围的考勤记录
     */
    List<AttendanceRecord> getRecordsByEmployeeIdAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);
}
