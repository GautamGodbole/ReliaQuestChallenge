package com.example.rqchallenge.employees.controllers;

import com.example.rqchallenge.employees.exceptions.EmployeeNotFoundException;
import com.example.rqchallenge.employees.services.EmployeeService;
import com.example.rqchallenge.employees.dtos.EmployeeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.min;

/*
 * Class : EmployeeControllerImpl
 *
 * Implements endpoints declared in Interface EmployeeController.
 */
@RestController
@RequestMapping(path = "/api/v1/employees")
public class EmployeeControllerImpl implements EmployeeController {

    private static final Logger log = LogManager.getLogger(EmployeeControllerImpl.class);
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeControllerImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    // Returns all employee records
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        String fcn = "getAllEmployees:";

        List<EmployeeDto> allEmployees = employeeService.getAllEmployees();

        return new ResponseEntity<>(allEmployees, HttpStatus.OK);
    }

    @Override
    // Filters employees by given name
    public ResponseEntity<List<EmployeeDto>> getEmployeesByNameSearch(String searchString) {
        String fcn = "getEmployeesByNameSearch:";

        log.info(fcn + "searchString : " + searchString);
        List<EmployeeDto> filteredEmployeesByName = employeeService.getEmployeesByNameSearch(searchString);
        if(filteredEmployeesByName.isEmpty())
            return new ResponseEntity<>(filteredEmployeesByName, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(filteredEmployeesByName, HttpStatus.OK);
    }

    @Override
    // Filters employees by given id
    public ResponseEntity<EmployeeDto> getEmployeeByIdSearch(String id) {
        String fcn = "getEmployeeByIdSearch:";

        log.info(fcn + "id : " + id);
        EmployeeDto employee = employeeService.getEmployeeByIdSearch(id);
        if(employee == null)
            return new ResponseEntity<>(employee, HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @Override
    // Return highest salary from employees records
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        String fcn = "getHighestSalaryOfEmployees:";

        List<EmployeeDto> sortedBySalaryDescending = employeeService.getEmployeesBySalaryOrdering(EmployeeService.SALARY_ORDERING.DESCENDING);

        if ((sortedBySalaryDescending != null) && (!sortedBySalaryDescending.isEmpty()))
            return ResponseEntity.ok(Integer.valueOf(sortedBySalaryDescending.get(0).getSalary()));
        else
            return ResponseEntity.ok(null);
    }

    @Override
    // Returns the top10 highest salaries from employees data
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        String fcn = "getTopTenHighestEarningEmployeeNames:";
        List<String> top10Salaries = new ArrayList<>();
        int i, nEmployees;

        List<EmployeeDto> sortedBySalaryDescending = employeeService.getEmployeesBySalaryOrdering(EmployeeService.SALARY_ORDERING.DESCENDING);
        if ((sortedBySalaryDescending != null) && (!sortedBySalaryDescending.isEmpty())) {
            nEmployees = min(sortedBySalaryDescending.size(), 10);
            for (i = 0; i < nEmployees; i++)
                top10Salaries.add(sortedBySalaryDescending.get(i).getSalary());
        }

        return new ResponseEntity<>(top10Salaries, HttpStatus.OK);
    }

    @Override
    // Creates a new employee record if the fields are valid
    public ResponseEntity<EmployeeDto> createEmployee(Map<String, Object> employeeInput) {
        String fcn = "createEmployee:";
        ObjectMapper objectMapper = new ObjectMapper();

        EmployeeDto newEmployee = objectMapper.convertValue(employeeInput, EmployeeDto.class);
        EmployeeDto confirmation = employeeService.createNewEmployee(newEmployee);
        log.info(fcn + confirmation.toString());

        return new ResponseEntity<>(confirmation, HttpStatus.CREATED);
    }

    @Override
    // Deletes an employee with given id (if present)
    public ResponseEntity<String> deleteEmployeeById(String id) {
        String fcn = "deleteEmployeeById:";

        log.info(fcn + "id : " + id);
        employeeService.deleteEmployeeById(id);

        return new ResponseEntity<>("Success!", HttpStatus.OK);
    }
}
