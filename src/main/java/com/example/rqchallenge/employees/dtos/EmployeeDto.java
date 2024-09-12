package com.example.rqchallenge.employees.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Class : EmployeeDto
 *
 * Used to store/map the Employee record
 */
public class EmployeeDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("employee_name")
    private String name;

    @JsonProperty("employee_salary")
    private String salary;

    @JsonProperty("employee_age")
    private String age;

    @JsonProperty("profile_image")
    private String image;

    public EmployeeDto(String id, String name, String salary, String age, String image) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.image = image;
    }

    public EmployeeDto() {
        this.id = this.name = this.salary = this.age = this.image = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "EmployeeDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", salary='" + salary + '\'' +
                ", age='" + age + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}