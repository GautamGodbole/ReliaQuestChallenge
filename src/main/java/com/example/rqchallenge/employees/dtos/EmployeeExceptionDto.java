package com.example.rqchallenge.employees.dtos;

import org.springframework.http.HttpStatus;

/*
 * Class : EmployeeExceptionDto
 *
 * Used to send customized error messages.
 */
public class EmployeeExceptionDto {

    private int errorCode;

    private String errorMessage;

    public EmployeeExceptionDto(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "EmployeeExceptionDto{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
