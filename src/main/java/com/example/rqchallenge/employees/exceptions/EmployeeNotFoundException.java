package com.example.rqchallenge.employees.exceptions;

/*
 * Class : EmployeeNotFoundException
 *
 * Custom Exception class to provide customized error messages
 */
public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
