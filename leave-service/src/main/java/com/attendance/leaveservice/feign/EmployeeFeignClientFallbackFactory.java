package com.attendance.leaveservice.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Feign 熔断降级工厂
 * 当 employee-service 不可用时，执行降级逻辑
 */
@Slf4j
@Component
public class EmployeeFeignClientFallbackFactory implements FallbackFactory<EmployeeFeignClient> {

    @Override
    public EmployeeFeignClient create(Throwable cause) {
        log.error("调用 employee-service 失败，触发熔断降级: {}", cause.getMessage());

        return new EmployeeFeignClient() {
            @Override
            public Map<String, Object> getEmployeeById(Long id) {
                log.warn("降级：获取员工信息失败，ID: {}", id);
                Map<String, Object> fallback = new HashMap<>();
                fallback.put("id", id);
                fallback.put("name", "未知员工");
                fallback.put("employeeNo", "N/A");
                return fallback;
            }

            @Override
            public String test() {
                return "employee-service 降级响应：服务不可用";
            }
        };
    }
}
