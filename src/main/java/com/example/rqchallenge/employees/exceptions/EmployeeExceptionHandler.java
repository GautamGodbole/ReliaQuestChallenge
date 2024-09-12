package com.example.rqchallenge.employees.exceptions;

import com.example.rqchallenge.employees.dtos.EmployeeExceptionDto;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

/*
 * Class : EmployeeExceptionHandler
 *
 * Acts as a global exception handler
 */
@RestControllerAdvice
public class EmployeeExceptionHandler {

    private static final Logger log = LogManager.getLogger(EmployeeExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpClientErrorException.class})
    public EmployeeExceptionDto handleException(HttpClientErrorException clientErrorException) {
        log.error(clientErrorException);
        return new EmployeeExceptionDto(clientErrorException.getStatusCode().value(), clientErrorException.getStatusText());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({EmployeeFieldsNotValidException.class})
    public EmployeeExceptionDto handleException(EmployeeFieldsNotValidException employeeFieldsNotValidException) {
        log.error(employeeFieldsNotValidException);
        return new EmployeeExceptionDto(HttpStatus.BAD_REQUEST.value(), employeeFieldsNotValidException.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({EmployeeNotFoundException.class})
    public EmployeeExceptionDto handleException(EmployeeNotFoundException employeeNotFoundException) {
        log.error(employeeNotFoundException);
        return new EmployeeExceptionDto(HttpStatus.NOT_FOUND.value(), employeeNotFoundException.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    public EmployeeExceptionDto handleException(IllegalArgumentException illegalArgumentException) {
        log.error(illegalArgumentException);
        return new EmployeeExceptionDto(HttpStatus.BAD_REQUEST.value(), illegalArgumentException.getMessage());
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler({RestClientException.class})
    public EmployeeExceptionDto handleException(RestClientException restClientException) {
        log.error(restClientException);
        return new EmployeeExceptionDto(HttpStatus.SERVICE_UNAVAILABLE.value(), restClientException.getMessage());
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler({IOException.class})
    public EmployeeExceptionDto handleException(IOException ioException) {
        log.error(ioException);
        return new EmployeeExceptionDto(HttpStatus.SERVICE_UNAVAILABLE.value(), ioException.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({UnrecognizedPropertyException.class})
    public EmployeeExceptionDto handleException(UnrecognizedPropertyException unrecognizedPropertyException) {
        log.error(unrecognizedPropertyException);
        return new EmployeeExceptionDto(HttpStatus.BAD_REQUEST.value(), unrecognizedPropertyException.getMessage());
    }
}
