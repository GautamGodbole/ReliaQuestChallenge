package com.example.rqchallenge.employees.controllers;

import com.example.rqchallenge.employees.dtos.EmployeeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
 * Interface : EmployeeController
 *
 * Declares the APIs that are exposed to world
 */
public interface EmployeeController {

    @GetMapping()
    ResponseEntity<List<EmployeeDto>> getAllEmployees();

    @GetMapping("/search/{searchString}")
    ResponseEntity<List<EmployeeDto>> getEmployeesByNameSearch(@PathVariable String searchString);

    @GetMapping("/{id}")
    ResponseEntity<EmployeeDto> getEmployeeByIdSearch(@PathVariable String id);

    @GetMapping("/highestSalary")
    ResponseEntity<Integer> getHighestSalaryOfEmployees();

    @GetMapping("/topTenHighestEarningEmployeeNames")
    ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames();

    @PostMapping()
    ResponseEntity<EmployeeDto> createEmployee(@RequestBody Map<String, Object> employeeInput);

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteEmployeeById(@PathVariable String id);
}
