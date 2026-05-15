package com.attendance.employeeservice.service;

import com.attendance.employeeservice.entity.Employee;

import java.util.List;

/**
 * 员工服务接口
 */
public interface EmployeeService {

    /**
     * 创建员工
     */
    Employee createEmployee(Employee employee);

    /**
     * 根据ID获取员工
     */
    Employee getEmployeeById(Long id);

    /**
     * 根据员工编号获取员工
     */
    Employee getEmployeeByNo(String employeeNo);

    /**
     * 根据用户名获取员工
     */
    Employee getEmployeeByUsername(String username);

    /**
     * 获取所有员工
     */
    List<Employee> getAllEmployees();

    /**
     * 根据部门获取员工列表
     */
    List<Employee> getEmployeesByDepartment(String department);

    /**
     * 更新员工信息
     */
    Employee updateEmployee(Long id, Employee employee);

    /**
     * 删除员工
     */
    void deleteEmployee(Long id);

    /**
     * 员工登录验证
     */
    Employee login(String username, String password);
}
