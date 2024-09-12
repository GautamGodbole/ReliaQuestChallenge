package com.example.rqchallenge.employees.controllers;

import com.example.rqchallenge.employees.dtos.EmployeeDto;
import com.example.rqchallenge.employees.exceptions.EmployeeFieldsNotValidException;
import com.example.rqchallenge.employees.services.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Executable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerImplTest {

    @Mock
    private EmployeeService mockEmployeeService;

    @InjectMocks
    private EmployeeControllerImpl underTestController;

    @Test
    void getAllEmployees() {

        // 1 : getAllEmployees Success (non-zero records)
        List<EmployeeDto> expected1 = new ArrayList<>();
        expected1.add(new EmployeeDto("", "a", "", "", ""));
        when(mockEmployeeService.getAllEmployees()).thenReturn(expected1);
        ResponseEntity<List<EmployeeDto>> response1 = underTestController.getAllEmployees();
        List<EmployeeDto> actual1 = response1.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response1.getStatusCode()),
                () -> assertNotNull(actual1)
        );

        // 2 : getAllEmployees Success (zero records)
        List<EmployeeDto> expected2 = new ArrayList<>();
        when(mockEmployeeService.getAllEmployees()).thenReturn(expected2);
        ResponseEntity<List<EmployeeDto>> response2 = underTestController.getAllEmployees();
        List<EmployeeDto> actual2 = response2.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response2.getStatusCode())
        );
    }

    @Test
    void getEmployeesByNameSearch() {

        // 1 : getEmployeesByNameSearch Success (name : tatyana)
        String name1 = "Tatyana";
        List<EmployeeDto> expected1 = new ArrayList<>();
        expected1.add(new EmployeeDto("", name1, "", "", ""));
        when(mockEmployeeService.getEmployeesByNameSearch(name1)).thenReturn(expected1);
        ResponseEntity<List<EmployeeDto>> response1 = underTestController.getEmployeesByNameSearch(name1);
        List<EmployeeDto> actual1 = response1.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response1.getStatusCode()),
                () -> assertNotNull(actual1),
                () -> assertEquals(expected1.get(0).getName(), actual1.get(0).getName())
        );

        // 2 : getEmployeesByNameSearch Failure (name not found in employee records)
        String name2 = "abcde";
        List<EmployeeDto> expected2 = new ArrayList<>();
        when(mockEmployeeService.getEmployeesByNameSearch(name2)).thenReturn(expected2);
        ResponseEntity<List<EmployeeDto>> response2 = underTestController.getEmployeesByNameSearch(name2);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode())
        );
    }

    @Test
    void getEmployeeByIdSearch() {

        // 1 : getEmployeeByIdSearch Success (Id is present)
        String id1 = "15";
        EmployeeDto expected1 = new EmployeeDto("", "", "", "", "");
        when(mockEmployeeService.getEmployeeByIdSearch(id1)).thenReturn(expected1);
        ResponseEntity<EmployeeDto> response1 = underTestController.getEmployeeByIdSearch(id1);
        EmployeeDto actual1 = response1.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response1.getStatusCode()),
                () -> assertNotNull(actual1)
        );

        // 2 : getEmployeeByIdSearch Failure (Id is not present)
        String id2 = "10000";
        when(mockEmployeeService.getEmployeeByIdSearch(id2)).thenReturn(null);
        ResponseEntity<EmployeeDto> response2 = underTestController.getEmployeeByIdSearch(id2);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response2.getStatusCode())
        );
    }

    @Test
    void getHighestSalaryOfEmployees() {

        // 1 : getHighestSalaryOfEmployees Success (non-zero records)
        Integer salary1 = 12345;
        List<EmployeeDto> expected1 = new ArrayList<>();
        expected1.add(new EmployeeDto("", "", salary1.toString(), "", ""));
        when(mockEmployeeService.getEmployeesBySalaryOrdering(EmployeeService.SALARY_ORDERING.DESCENDING)).thenReturn(expected1);
        ResponseEntity<Integer> response1 = underTestController.getHighestSalaryOfEmployees();
        Integer actual1 = response1.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response1.getStatusCode()),
                () -> assertNotNull(actual1),
                () -> assertEquals(actual1, salary1)
        );

        // 2 : getHighestSalaryOfEmployees Success (zero records)
        List<EmployeeDto> expected2 = new ArrayList<>();
        when(mockEmployeeService.getEmployeesBySalaryOrdering(EmployeeService.SALARY_ORDERING.DESCENDING)).thenReturn(expected2);
        ResponseEntity<Integer> response2 = underTestController.getHighestSalaryOfEmployees();
        Integer actual2 = response2.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response2.getStatusCode())
        );
    }

    @Test
    void getTopTenHighestEarningEmployeeNames() {

        // 1 : getTopTenHighestEarningEmployeeNames Success (non-zero records)
        List<EmployeeDto> expected1 = new ArrayList<>();
        expected1.add(new EmployeeDto("", "", "12345", "", ""));
        when(mockEmployeeService.getEmployeesBySalaryOrdering(EmployeeService.SALARY_ORDERING.DESCENDING)).thenReturn(expected1);
        ResponseEntity<List<String>> response1 = underTestController.getTopTenHighestEarningEmployeeNames();
        List<String> actual1 = response1.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response1.getStatusCode()),
                () -> assertNotNull(actual1)
        );

        // 2 : getTopTenHighestEarningEmployeeNames Success (zero records)
        List<EmployeeDto> expected2 = new ArrayList<>();
        when(mockEmployeeService.getEmployeesBySalaryOrdering(EmployeeService.SALARY_ORDERING.DESCENDING)).thenReturn(expected2);
        ResponseEntity<List<String>> response2 = underTestController.getTopTenHighestEarningEmployeeNames();
        List<String> actual2 = response2.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response2.getStatusCode())
        );
    }

    @Test
    void createEmployee() {

        // 1 : createEmployee Success (new record)
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> employee1 = new HashMap<>();
        employee1.put("employee_name", "dummy");
        employee1.put("employee_salary", "12345");
        employee1.put("employee_age", "30");
        EmployeeDto expected1 = objectMapper.convertValue(employee1, EmployeeDto.class);
        when(mockEmployeeService.createNewEmployee(any(EmployeeDto.class))).thenReturn(expected1);
        ResponseEntity<EmployeeDto> response1 = underTestController.createEmployee(employee1);
        EmployeeDto actual1 = response1.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response1.getStatusCode()),
                () -> assertNotNull(actual1),
                () -> assertEquals(actual1.getName(), expected1.getName())
        );
    }

    @Test
    void deleteEmployeeById() {

        // 1 : deleteEmployeeById Success
        String id1 = "15";
        String expected1 = "Success!";
        doNothing().when(mockEmployeeService).deleteEmployeeById(id1);
        ResponseEntity<String> response1 = underTestController.deleteEmployeeById(id1);
        String actual1 = response1.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response1.getStatusCode()),
                () -> assertEquals(actual1, expected1)
        );
    }
}