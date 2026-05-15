package com.attendance.attendanceservice.controller;

import com.attendance.attendanceservice.dto.Result;
import com.attendance.attendanceservice.entity.AttendanceRecord;
import com.attendance.attendanceservice.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤控制器
 */
@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public String test() {
        return "attendance-service is running";
    }

    /**
     * 上班打卡
     */
    @PostMapping("/clock-in/{employeeId}")
    public Result<AttendanceRecord> clockIn(@PathVariable Long employeeId) {
        try {
            AttendanceRecord record = attendanceService.clockIn(employeeId);
            return Result.success("上班打卡成功", record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 下班打卡
     */
    @PostMapping("/clock-out/{employeeId}")
    public Result<AttendanceRecord> clockOut(@PathVariable Long employeeId) {
        try {
            AttendanceRecord record = attendanceService.clockOut(employeeId);
            return Result.success("下班打卡成功", record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取考勤记录
     */
    @GetMapping("/record/{id}")
    public Result<AttendanceRecord> getRecordById(@PathVariable Long id) {
        try {
            AttendanceRecord record = attendanceService.getRecordById(id);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取员工今日考勤
     */
    @GetMapping("/today/{employeeId}")
    public Result<AttendanceRecord> getTodayRecord(@PathVariable Long employeeId) {
        AttendanceRecord record = attendanceService.getTodayRecord(employeeId);
        return Result.success(record);
    }

    /**
     * 获取员工考勤记录列表
     */
    @GetMapping("/employee/{employeeId}")
    public Result<List<AttendanceRecord>> getRecordsByEmployeeId(@PathVariable Long employeeId) {
        List<AttendanceRecord> records = attendanceService.getRecordsByEmployeeId(employeeId);
        return Result.success(records);
    }

    /**
     * 获取指定日期的考勤记录
     */
    @GetMapping("/date/{date}")
    public Result<List<AttendanceRecord>> getRecordsByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<AttendanceRecord> records = attendanceService.getRecordsByDate(date);
        return Result.success(records);
    }

    /**
     * 获取员工指定日期范围的考勤记录
     */
    @GetMapping("/employee/{employeeId}/range")
    public Result<List<AttendanceRecord>> getRecordsByDateRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<AttendanceRecord> records = attendanceService.getRecordsByEmployeeIdAndDateRange(employeeId, startDate, endDate);
        return Result.success(records);
    }
}
