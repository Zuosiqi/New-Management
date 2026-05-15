package com.attendance.employeeservice.service.impl;

import com.attendance.employeeservice.entity.Employee;
import com.attendance.employeeservice.repository.EmployeeRepository;
import com.attendance.employeeservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 员工服务实现类
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public Employee createEmployee(Employee employee) {
        // 检查用户名是否已存在
        if (employeeRepository.findByUsername(employee.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在: " + employee.getUsername());
        }
        // 检查员工编号是否已存在
        if (employeeRepository.findByEmployeeNo(employee.getEmployeeNo()).isPresent()) {
            throw new RuntimeException("员工编号已存在: " + employee.getEmployeeNo());
        }
        return employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("员工不存在，ID: " + id));
    }

    @Override
    public Employee getEmployeeByNo(String employeeNo) {
        return employeeRepository.findByEmployeeNo(employeeNo)
                .orElseThrow(() -> new RuntimeException("员工不存在，编号: " + employeeNo));
    }

    @Override
    public Employee getEmployeeByUsername(String username) {
        return employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("员工不存在，用户名: " + username));
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    @Override
    @Transactional
    public Employee updateEmployee(Long id, Employee employee) {
        Employee existing = getEmployeeById(id);

        // 更新字段
        if (employee.getName() != null) {
            existing.setName(employee.getName());
        }
        if (employee.getEmail() != null) {
            existing.setEmail(employee.getEmail());
        }
        if (employee.getPhone() != null) {
            existing.setPhone(employee.getPhone());
        }
        if (employee.getDepartment() != null) {
            existing.setDepartment(employee.getDepartment());
        }
        if (employee.getPosition() != null) {
            existing.setPosition(employee.getPosition());
        }
        if (employee.getStatus() != null) {
            existing.setStatus(employee.getStatus());
        }

        return employeeRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        // 软删除：将状态设为离职
        employee.setStatus(0);
        employeeRepository.save(employee);
    }

    @Override
    public Employee login(String username, String password) {
        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 简单密码验证（实际项目中应使用加密）
        if (!employee.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (employee.getStatus() != 1) {
            throw new RuntimeException("该账号已被禁用");
        }

        return employee;
    }
}
