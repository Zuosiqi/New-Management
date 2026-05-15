package com.attendance.attendanceservice.service.impl;

import com.attendance.attendanceservice.entity.AttendanceRecord;
import com.attendance.attendanceservice.feign.EmployeeFeignClient;
import com.attendance.attendanceservice.repository.AttendanceRecordRepository;
import com.attendance.attendanceservice.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 考勤服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRecordRepository recordRepository;
    private final EmployeeFeignClient employeeFeignClient;

    // 上班时间定义：9:00
    private static final LocalTime WORK_START_TIME = LocalTime.of(9, 0);
    // 下班时间定义：18:00
    private static final LocalTime WORK_END_TIME = LocalTime.of(18, 0);

    @Override
    @Transactional
    public AttendanceRecord clockIn(Long employeeId) {
        LocalDate today = LocalDate.now();

        // 检查今天是否已打卡
        Optional<AttendanceRecord> existing = recordRepository.findByEmployeeIdAndDate(employeeId, today);
        if (existing.isPresent() && existing.get().getClockInTime() != null) {
            throw new RuntimeException("今日已打卡，无需重复打卡");
        }

        // 通过 Feign 调用获取员工信息
        Map employeeInfo = getEmployeeInfo(employeeId);
        String employeeNo = (String) employeeInfo.get("employeeNo");
        String employeeName = (String) employeeInfo.get("name");

        AttendanceRecord record;
        if (existing.isPresent()) {
            record = existing.get();
        } else {
            record = new AttendanceRecord();
            record.setEmployeeId(employeeId);
            record.setEmployeeNo(employeeNo);
            record.setEmployeeName(employeeName);
            record.setDate(today);
        }

        LocalDateTime now = LocalDateTime.now();
        record.setClockInTime(now);

        // 判断是否迟到
        if (now.toLocalTime().isAfter(WORK_START_TIME)) {
            record.setStatus(2); // 迟到
            log.info("员工 {} 迟到，打卡时间: {}", employeeName, now);
        } else {
            record.setStatus(0); // 未完成（等待下班打卡）
        }

        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public AttendanceRecord clockOut(Long employeeId) {
        LocalDate today = LocalDate.now();

        AttendanceRecord record = recordRepository.findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new RuntimeException("今日未上班打卡，请先进行上班打卡"));

        if (record.getClockOutTime() != null) {
            throw new RuntimeException("今日已下班打卡，无需重复打卡");
        }

        LocalDateTime now = LocalDateTime.now();
        record.setClockOutTime(now);

        // 判断是否早退
        if (now.toLocalTime().isBefore(WORK_END_TIME)) {
            record.setStatus(3); // 早退
            log.info("员工 {} 早退，打卡时间: {}", record.getEmployeeName(), now);
        } else {
            // 如果之前没有迟到，标记为正常
            if (record.getStatus() != 2) {
                record.setStatus(1); // 正常
            }
        }

        return recordRepository.save(record);
    }

    @Override
    public AttendanceRecord getRecordById(Long id) {
        return recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("考勤记录不存在，ID: " + id));
    }

    @Override
    public AttendanceRecord getTodayRecord(Long employeeId) {
        return recordRepository.findByEmployeeIdAndDate(employeeId, LocalDate.now())
                .orElse(null);
    }

    @Override
    public List<AttendanceRecord> getRecordsByEmployeeId(Long employeeId) {
        return recordRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<AttendanceRecord> getRecordsByDate(LocalDate date) {
        return recordRepository.findByDate(date);
    }

    @Override
    public List<AttendanceRecord> getRecordsByEmployeeIdAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return recordRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);
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
