package com.example.rqchallenge.employees.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Class : EmployeeServiceDto
 *
 * Used to map the response received from External API
 */
public class EmployeeServiceDto {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private Object data;

    @JsonProperty("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "EmployeeServiceDto{" +
                "status='" + status + '\'' +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
