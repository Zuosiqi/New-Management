package com.attendance.employeeservice.controller;

import com.attendance.employeeservice.dto.Result;
import com.attendance.employeeservice.entity.Employee;
import com.attendance.employeeservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工控制器
 */
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public String test() {
        return "employee-service is running";
    }

    /**
     * 创建员工
     */
    @PostMapping
    public Result<Employee> createEmployee(@RequestBody Employee employee) {
        try {
            Employee created = employeeService.createEmployee(employee);
            return Result.success("创建成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取员工
     */
    @GetMapping("/{id}")
    public Result<Employee> getEmployeeById(@PathVariable Long id) {
        try {
            Employee employee = employeeService.getEmployeeById(id);
            return Result.success(employee);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据员工编号获取员工
     */
    @GetMapping("/no/{employeeNo}")
    public Result<Employee> getEmployeeByNo(@PathVariable String employeeNo) {
        try {
            Employee employee = employeeService.getEmployeeByNo(employeeNo);
            return Result.success(employee);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有员工
     */
    @GetMapping("/list")
    public Result<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return Result.success(employees);
    }

    /**
     * 根据部门获取员工列表
     */
    @GetMapping("/department/{department}")
    public Result<List<Employee>> getEmployeesByDepartment(@PathVariable String department) {
        List<Employee> employees = employeeService.getEmployeesByDepartment(department);
        return Result.success(employees);
    }

    /**
     * 更新员工信息
     */
    @PutMapping("/{id}")
    public Result<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        try {
            Employee updated = employeeService.updateEmployee(id, employee);
            return Result.success("更新成功", updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除员工（软删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 员工登录
     */
    @PostMapping("/login")
    public Result<Employee> login(@RequestParam String username, @RequestParam String password) {
        try {
            Employee employee = employeeService.login(username, password);
            return Result.success("登录成功", employee);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
