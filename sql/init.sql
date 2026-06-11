-- 员工考勤管理系统 数据库初始化脚本
-- 说明：
-- 1. 当前仓库中 employee-service、attendance-service、leave-service 分别使用独立数据库。
-- 2. attendance-service 代码中仍包含请假实体和接口，因此 db_attendance 中保留 leave_request 表。
-- 3. leave-service 也有自己独立的 leave_request 表，因此 db_leave 需要单独初始化。


-- =============================================
-- 0. 创建三个业务数据库
-- =============================================
CREATE DATABASE IF NOT EXISTS `db_employee` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `db_attendance` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `db_leave` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =============================================
-- 1. 员工数据库 (employee-service 使用)
-- =============================================
USE `db_employee`;

CREATE TABLE IF NOT EXISTS `employee` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '员工ID',
    `employee_no` VARCHAR(20) NOT NULL COMMENT '员工编号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录用）',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `department` VARCHAR(50) DEFAULT NULL COMMENT '部门',
    `position` VARCHAR(50) DEFAULT NULL COMMENT '职位',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-在职，0-离职',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_no` (`employee_no`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表';

INSERT INTO `employee` (`id`, `employee_no`, `name`, `username`, `password`, `email`, `phone`, `department`, `position`, `status`)
VALUES
    (1, 'EMP001', '张三', 'zhangsan', '123456', 'zhangsan@example.com', '13800138001', '技术部', 'Java开发工程师', 1),
    (2, 'EMP002', '李四', 'lisi', '123456', 'lisi@example.com', '13800138002', '技术部', '前端开发工程师', 1),
    (3, 'EMP003', '王五', 'wangwu', '123456', 'wangwu@example.com', '13800138003', '产品部', '产品经理', 1),
    (4, 'EMP004', '赵六', 'zhaoliu', '123456', 'zhaoliu@example.com', '13800138004', '人事部', 'HR专员', 1),
    (5, 'EMP005', '钱七', 'qianqi', '123456', 'qianqi@example.com', '13800138005', '技术部', '测试工程师', 1)
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `password` = VALUES(`password`),
    `email` = VALUES(`email`),
    `phone` = VALUES(`phone`),
    `department` = VALUES(`department`),
    `position` = VALUES(`position`),
    `status` = VALUES(`status`);

-- =============================================
-- 2. 考勤数据库 (attendance-service 使用)
-- =============================================
USE `db_attendance`;

CREATE TABLE IF NOT EXISTS `attendance_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `employee_no` VARCHAR(20) NOT NULL COMMENT '员工编号',
    `employee_name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
    `date` DATE NOT NULL COMMENT '考勤日期',
    `clock_in_time` DATETIME DEFAULT NULL COMMENT '上班打卡时间',
    `clock_out_time` DATETIME DEFAULT NULL COMMENT '下班打卡时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-未打卡，1-正常，2-迟到，3-早退，4-缺勤',
    `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_date` (`date`),
    UNIQUE KEY `uk_employee_date` (`employee_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考勤记录表';

CREATE TABLE IF NOT EXISTS `leave_request` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `employee_no` VARCHAR(20) NOT NULL COMMENT '员工编号',
    `employee_name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
    `leave_type` VARCHAR(20) NOT NULL COMMENT '请假类型：事假/病假/年假/调休',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `days` INT NOT NULL COMMENT '请假天数',
    `reason` VARCHAR(500) NOT NULL COMMENT '请假原因',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审批，1-已批准，2-已拒绝',
    `approver_id` BIGINT DEFAULT NULL COMMENT '审批人ID',
    `approver_name` VARCHAR(50) DEFAULT NULL COMMENT '审批人姓名',
    `approve_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `approve_remark` VARCHAR(200) DEFAULT NULL COMMENT '审批备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='attendance-service 使用的请假申请表';

INSERT INTO `attendance_record` (`id`, `employee_id`, `employee_no`, `employee_name`, `date`, `clock_in_time`, `clock_out_time`, `status`, `remark`)
VALUES
    (1, 1, 'EMP001', '张三', '2024-01-15', '2024-01-15 08:55:00', '2024-01-15 18:05:00', 1, NULL),
    (2, 1, 'EMP001', '张三', '2024-01-16', '2024-01-16 09:10:00', '2024-01-16 18:00:00', 2, '上班迟到'),
    (3, 2, 'EMP002', '李四', '2024-01-15', '2024-01-15 08:50:00', '2024-01-15 18:10:00', 1, NULL),
    (4, 2, 'EMP002', '李四', '2024-01-16', '2024-01-16 08:58:00', '2024-01-16 17:30:00', 3, '下班早退')
ON DUPLICATE KEY UPDATE
    `employee_no` = VALUES(`employee_no`),
    `employee_name` = VALUES(`employee_name`),
    `clock_in_time` = VALUES(`clock_in_time`),
    `clock_out_time` = VALUES(`clock_out_time`),
    `status` = VALUES(`status`),
    `remark` = VALUES(`remark`);

INSERT INTO `leave_request` (`id`, `employee_id`, `employee_no`, `employee_name`, `leave_type`, `start_date`, `end_date`, `days`, `reason`, `status`, `approver_id`, `approver_name`, `approve_time`, `approve_remark`)
VALUES
    (1, 3, 'EMP003', '王五', '年假', '2024-01-20', '2024-01-22', 3, '回老家过年', 1, 4, '赵六', '2024-01-18 10:00:00', '同意'),
    (2, 1, 'EMP001', '张三', '病假', '2024-01-18', '2024-01-18', 1, '感冒发烧', 0, NULL, NULL, NULL, NULL)
ON DUPLICATE KEY UPDATE
    `employee_no` = VALUES(`employee_no`),
    `employee_name` = VALUES(`employee_name`),
    `leave_type` = VALUES(`leave_type`),
    `start_date` = VALUES(`start_date`),
    `end_date` = VALUES(`end_date`),
    `days` = VALUES(`days`),
    `reason` = VALUES(`reason`),
    `status` = VALUES(`status`),
    `approver_id` = VALUES(`approver_id`),
    `approver_name` = VALUES(`approver_name`),
    `approve_time` = VALUES(`approve_time`),
    `approve_remark` = VALUES(`approve_remark`);

-- =============================================
-- 3. 请假数据库 (leave-service 使用)
-- =============================================
USE `db_leave`;

CREATE TABLE IF NOT EXISTS `leave_request` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    `employee_id` BIGINT NOT NULL COMMENT '员工ID',
    `leave_type` VARCHAR(20) NOT NULL COMMENT '请假类型',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '请假原因',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/APPROVED/REJECTED',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='leave-service 使用的请假申请表';

INSERT INTO `leave_request` (`id`, `employee_id`, `leave_type`, `start_time`, `end_time`, `reason`, `status`, `create_time`)
VALUES
    (1, 3, 'ANNUAL', '2024-01-20 09:00:00', '2024-01-22 18:00:00', '回老家过年', 'APPROVED', '2024-01-18 10:00:00'),
    (2, 1, 'SICK', '2024-01-18 09:00:00', '2024-01-18 18:00:00', '感冒发烧', 'PENDING', '2024-01-18 08:30:00')
ON DUPLICATE KEY UPDATE
    `employee_id` = VALUES(`employee_id`),
    `leave_type` = VALUES(`leave_type`),
    `start_time` = VALUES(`start_time`),
    `end_time` = VALUES(`end_time`),
    `reason` = VALUES(`reason`),
    `status` = VALUES(`status`),
    `create_time` = VALUES(`create_time`);
