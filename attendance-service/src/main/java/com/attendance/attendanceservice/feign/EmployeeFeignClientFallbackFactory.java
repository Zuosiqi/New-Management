package com.attendance.attendanceservice.feign;

import com.attendance.attendanceservice.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

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
            public Result<Map> getEmployeeById(Long id) {
                log.warn("降级：获取员工信息失败，ID: {}", id);
                return Result.error(503, "员工服务暂时不可用，请稍后重试");
            }

            @Override
            public Result<Map> getEmployeeByNo(String employeeNo) {
                log.warn("降级：获取员工信息失败，编号: {}", employeeNo);
                return Result.error(503, "员工服务暂时不可用，请稍后重试");
            }

            @Override
            public String test() {
                return "employee-service 降级响应：服务不可用";
            }
        };
    }
}
