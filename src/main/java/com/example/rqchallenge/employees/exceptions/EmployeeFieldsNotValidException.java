package com.example.rqchallenge.employees.exceptions;

/*
 * Class : EmployeeFieldsNotValidException
 *
 * Custom Exception class to provide customized error messages
 */
public class EmployeeFieldsNotValidException extends RuntimeException {

    public EmployeeFieldsNotValidException(String message) {
        super(message);
    }
}
