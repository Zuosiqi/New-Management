package com.attendance.leaveservice.feign;

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
    Map<String, Object> getEmployeeById(@PathVariable("id") Long id);

    /**
     * 测试接口
     */
    @GetMapping("/employee/test")
    String test();
}
