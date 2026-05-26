package com.yh.service;

import com.yh.dto.EmployeeDTO;
import com.yh.dto.EmployeeLoginDTO;
import com.yh.dto.EmployeePageQueryDTO;
import com.yh.entity.Employee;
import com.yh.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void save(EmployeeDTO employeeDTO);

    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    void startOrStop(Integer status, Long id);

    Employee getById(Long id);

    void update(EmployeeDTO employeeDTO);
}
