package com.example.rqchallenge.employees.services;

import com.example.rqchallenge.employees.dtos.EmployeeServiceDto;
import com.example.rqchallenge.employees.dtos.EmployeeDto;
import com.example.rqchallenge.employees.exceptions.EmployeeFieldsNotValidException;
import com.example.rqchallenge.employees.exceptions.EmployeeNotFoundException;
import com.example.rqchallenge.employees.services.validators.EmployeeFieldsValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * Class : EmployeeService
 *
 * API exchange with external url (see BASE_URL)
 * If for some reason external API is unavailable (or sends 5xx response)
 * it will use BackupService for the specific operation.
 */
@Service
public class EmployeeService {

    private static final Logger log = LogManager.getLogger(EmployeeService.class);
    private final EmployeeFieldsValidator fieldsValidator;
    private final EmployeeBackupService backupService;
    private final RestTemplate restTemplate;
    private final String BASE_URL = "https://dummy.restapiexample.com";
    public enum SALARY_ORDERING {
        ASCENDING,
        DESCENDING
    }

    @Autowired
    public EmployeeService(EmployeeFieldsValidator fieldsValidator, EmployeeBackupService backupService, RestTemplate restTemplate) {
        this.fieldsValidator = fieldsValidator;
        this.backupService = backupService;
        this.restTemplate = restTemplate;

    }

    // Fetches all employees data from the external API
    public List<EmployeeDto> getAllEmployees() {
        String fcn = "getAllEmployees:";
        List<EmployeeDto> allEmployeesList = null;
        ObjectMapper objectMapper = new ObjectMapper();
        URI uri = null;

        // Ideally we should never get an exception here
        // Added the try/catch to make compiler happy
        try {
            uri = new URI (BASE_URL + "/api/v1/employees");
        } catch (URISyntaxException uriSyntaxException) {
            log.error(fcn, uriSyntaxException);
        }

        try {
            EmployeeServiceDto response = restTemplate.getForObject(uri, EmployeeServiceDto.class);
            log.info(fcn + response.toString());
            allEmployeesList = objectMapper.convertValue(response.getData(), new TypeReference<List<EmployeeDto>>() {});
        } catch (HttpServerErrorException | NullPointerException | IllegalArgumentException serverErrorException) {
            log.error(fcn, serverErrorException);
            allEmployeesList = backupService.getAllEmployees();
        }

        return allEmployeesList;
    }


    // Fetches all employees data from external API and filters it by employee name
    public List<EmployeeDto> getEmployeesByNameSearch(String searchString) {
        List<EmployeeDto> allEmployeesList = this.getAllEmployees();
        List<EmployeeDto> filteredEmployeesByName = new ArrayList<>();

        if (!fieldsValidator.isValidAlphaNumericString(searchString))
            throw new EmployeeFieldsNotValidException("Name not valid! Only AlphaNumeric characters are allowed");

        if ((allEmployeesList!= null) && (!allEmployeesList.isEmpty()) && (searchString != null)) {
            allEmployeesList.stream().filter(employee -> employee.getName().toLowerCase()
                    .contains(searchString.toLowerCase())).forEach(filteredEmployeesByName::add);
        }

        return filteredEmployeesByName;
    }

    // Fetches employee data specific to provided employee id
    public EmployeeDto getEmployeeByIdSearch(String id) {
        String fcn = "getEmployeeByIdSearch:";
        EmployeeDto employee = null;
        ObjectMapper objectMapper = new ObjectMapper();
        URI uri = null;

        if (!fieldsValidator.isValidNumber(id)) {
            throw new EmployeeFieldsNotValidException("Id not valid! Only numbers are allowed");
        }

        // Ideally we should never get an exception here
        // Added the try/catch to make compiler happy
        try {
            uri = new URI(BASE_URL + "/api/v1/employee/" + id);
        } catch (URISyntaxException uriSyntaxException) {
            log.error(fcn, uriSyntaxException);
        }

        try {
            EmployeeServiceDto response = restTemplate.getForObject(uri, EmployeeServiceDto.class);
            log.info(fcn + response.toString());
            employee = objectMapper.convertValue(response.getData(), EmployeeDto.class);
        } catch (HttpServerErrorException | NullPointerException | IllegalArgumentException serverErrorException) {
            log.error(fcn, serverErrorException);
            employee = backupService.getEmployeeByIdSearch(id);
        }

        return employee;
    }

    // Fetches all employees data and sorts it as per salary of each employee
    // Takes argument order and sorts it accordingly
    public List<EmployeeDto> getEmployeesBySalaryOrdering(SALARY_ORDERING order) {
        List<EmployeeDto> orderedEmployeesBySalary = this.getAllEmployees();
        Comparator<EmployeeDto> sortBySalaryLength = Comparator.comparingInt(e -> e.getSalary().length());
        Comparator<EmployeeDto> sortBySalary = sortBySalaryLength.thenComparing(EmployeeDto::getSalary);

        if ((orderedEmployeesBySalary != null) && (!orderedEmployeesBySalary.isEmpty())) {
            if (order == SALARY_ORDERING.ASCENDING)
                orderedEmployeesBySalary.sort(sortBySalary);
            else
                orderedEmployeesBySalary.sort(sortBySalary.reversed());
        }

        return orderedEmployeesBySalary;
    }

    // Sends POST request to external API to add  new employee
    public EmployeeDto createNewEmployee(EmployeeDto newEmployee) {
        String fcn = "createNewEmployee:";
        EmployeeDto createdEmployee = null;
        ObjectMapper objectMapper = new ObjectMapper();
        URI uri = null;

        if (!fieldsValidator.isValidEmployeeDto(newEmployee))
            throw new EmployeeFieldsNotValidException("Employee fields not valid!");

        // Ideally we should never get an exception here
        // Added the try/catch to make compiler happy
        try {
            uri = new URI(BASE_URL + "/api/v1/create");
        } catch (URISyntaxException uriSyntaxException) {
            log.error(fcn, uriSyntaxException);
        }

        try {
            EmployeeServiceDto response = restTemplate.postForObject(uri, newEmployee, EmployeeServiceDto.class);
            log.info(fcn + response.toString());
            createdEmployee = objectMapper.convertValue(response.getData(), EmployeeDto.class);
        } catch (HttpServerErrorException | NullPointerException | IllegalArgumentException serverErrorException) {
            log.error(fcn, serverErrorException);
            createdEmployee = backupService.createNewEmployee(newEmployee);
        }

        return createdEmployee;
    }

    // Sends DELETE request with employee id to delete an employee
    public void deleteEmployeeById(String id) {
        String fcn = "deleteEmployeeById:";
        URI uri = null;

        // Ideally we should never get an exception here
        // Added the try/catch to make compiler happy
        try {
            uri = new URI(BASE_URL + "/api/v1/delete/" + id);
        } catch (URISyntaxException uriSyntaxException) {
            log.error(fcn, uriSyntaxException);
        }

        if (!fieldsValidator.isValidNumber(id))
            throw new EmployeeFieldsNotValidException("Id not valid! Only numbers are allowed");

        try {
            restTemplate.delete(uri);
        } catch (HttpServerErrorException | NullPointerException | IllegalArgumentException serverErrorException) {
            log.error(fcn, serverErrorException);
            backupService.deleteEmployeeById(id);
        }
    }
}
