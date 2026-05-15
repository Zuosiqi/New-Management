package com.attendance.attendanceservice.feign;

import com.attendance.attendanceservice.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * 员工服务 Feign 客户端
 * 用于调用 employee-service 的接口
 *
 * name: 目标服务名称（对应 Eureka 中注册的服务名）
 * fallbackFactory: 熔断降级处理
 */
@FeignClient(name = "employee-service", fallbackFactory = EmployeeFeignClientFallbackFactory.class)
public interface EmployeeFeignClient {

    /**
     * 根据ID获取员工信息
     */
    @GetMapping("/employee/{id}")
    Result<Map> getEmployeeById(@PathVariable("id") Long id);

    /**
     * 根据员工编号获取员工信息
     */
    @GetMapping("/employee/no/{employeeNo}")
    Result<Map> getEmployeeByNo(@PathVariable("employeeNo") String employeeNo);

    /**
     * 测试接口
     */
    @GetMapping("/employee/test")
    String test();
}
