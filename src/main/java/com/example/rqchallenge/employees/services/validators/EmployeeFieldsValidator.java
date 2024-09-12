package com.example.rqchallenge.employees.services.validators;

import com.example.rqchallenge.employees.dtos.EmployeeDto;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Class : EmployeeFieldsValidator
 *
 * It validates various fields of Employee record
 */
@Service
public class EmployeeFieldsValidator {

    // Checks if field is null or ""
    public boolean isNullString (String field) {
        return ((field == null) || (field.isEmpty()));
    }

    // Checks if the field contains only alphanumeric characters
    public boolean isValidAlphaNumericString (String field) {
        String regex = "^[a-zA-Z0-9 ]+$";

        if (isNullString(field))
            return true;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(field);

        return matcher.find();
    }

    // Checks if field contains only numeric characters
    public boolean isValidNumber (String field) {
        String regex = "^[0-9]+$";

        if (isNullString(field))
            return true;

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(field);

        return matcher.find();
    }

    // Performs field validation on all fields of Employee
    public boolean isValidEmployeeDto (EmployeeDto employeeDto) {
        boolean valid;

        valid = isValidNumber(employeeDto.getId())
                    && isValidAlphaNumericString(employeeDto.getName())
                    && isValidNumber(employeeDto.getSalary())
                    && isValidNumber(employeeDto.getAge())
                    && isValidAlphaNumericString(employeeDto.getImage());

        return valid;
    }
}
