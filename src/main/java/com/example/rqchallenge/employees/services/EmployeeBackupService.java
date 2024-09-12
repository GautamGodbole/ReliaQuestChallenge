package com.example.rqchallenge.employees.services;

import com.example.rqchallenge.employees.dtos.EmployeeDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * Class : EmployeeBackupService
 *
 * It loads the employees data from a static file and
 * acts as an endpoint to perform various operations.
 */
@Service
public class EmployeeBackupService {

    private static final Logger log = LogManager.getLogger(EmployeeBackupService.class);
    private final ResourceLoader resourceLoader;
    private Resource resource;
    private final String employeesJsonFileName = "employees.json";
    private List<EmployeeDto> allEmployees = new ArrayList<>();

    @Autowired
    public EmployeeBackupService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
//        this.loadEmployees();
    }

    @PostConstruct
    // Fetches the employee data from static file and stores it internally
    public void loadEmployees() {
        String fcn = "loadEmployees:";
        ObjectMapper objectMapper = new ObjectMapper();
        resource = resourceLoader.getResource("classpath:" + employeesJsonFileName);

        try {
            allEmployees = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<EmployeeDto>>() {});
            log.info(fcn + allEmployees.size());
        } catch (IOException ioException) {
            log.error(fcn, ioException);
        }
    }

    // Returns all employees data
    public List<EmployeeDto> getAllEmployees() {
        return allEmployees;
    }

    // Searches employees data by given id
    public EmployeeDto getEmployeeByIdSearch(String id) {
        EmployeeDto defaultEmployee = null;
        Optional<EmployeeDto> matchedEmployee = allEmployees.stream().filter(employee -> employee.getId().equals(id)).findFirst();

        return matchedEmployee.orElse(defaultEmployee);
    }

    // Creates a new employee record
    public EmployeeDto createNewEmployee(EmployeeDto newEmployee) {
        newEmployee.setId(String.valueOf(System.currentTimeMillis()));
        allEmployees.add(newEmployee);

        return newEmployee;
    }

    // Deletes an employee record pertaining to given id
    public void deleteEmployeeById(String id) {
        allEmployees.removeIf(employee -> employee.getId().equals(id));
    }
}
