package com.attendance.employeeservice.repository;

import com.attendance.employeeservice.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 员工数据访问层
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * 根据用户名查找员工
     */
    Optional<Employee> findByUsername(String username);

    /**
     * 根据员工编号查找员工
     */
    Optional<Employee> findByEmployeeNo(String employeeNo);

    /**
     * 根据部门查找员工列表
     */
    List<Employee> findByDepartment(String department);

    /**
     * 根据状态查找员工列表
     */
    List<Employee> findByStatus(Integer status);

    /**
     * 根据姓名模糊查询
     */
    List<Employee> findByNameLike(String name);
}
