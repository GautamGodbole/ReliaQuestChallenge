package com.example.rqchallenge.employees.services;

import com.example.rqchallenge.employees.controllers.EmployeeController;
import com.example.rqchallenge.employees.controllers.EmployeeControllerImpl;
import com.example.rqchallenge.employees.dtos.EmployeeDto;
import com.example.rqchallenge.employees.dtos.EmployeeExceptionDto;
import com.example.rqchallenge.employees.dtos.EmployeeServiceDto;
import com.example.rqchallenge.employees.exceptions.EmployeeFieldsNotValidException;
import com.example.rqchallenge.employees.services.validators.EmployeeFieldsValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Executable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeFieldsValidator mockFieldsValidator;

    @Mock
    private EmployeeBackupService mockBackupService;

    @Mock
    private RestTemplate mockRestTemplate;

    @InjectMocks
    private EmployeeService underTestService;

    private final String BASE_URL = "https://dummy.restapiexample.com";

    @Test
    void getAllEmployees() throws URISyntaxException {

        URI uri = new URI (BASE_URL + "/api/v1/employees");

        // 1 : getAllEmployees Success (Returned from external API)
        EmployeeServiceDto expected1 = new EmployeeServiceDto();
        List<EmployeeDto> expectedEmployees1 = new ArrayList<>();
        expectedEmployees1.add(new EmployeeDto("1", "Tiger Nixon", "320800", "61", ""));
        expectedEmployees1.add(new EmployeeDto("2", "Garrett Winters", "170750", "63", ""));
        expected1.setData(expectedEmployees1);
        expected1.setStatus("Success");
        expected1.setMessage("All messages are retrieved");

        when(mockRestTemplate.getForObject(uri, EmployeeServiceDto.class)).thenReturn(expected1);
        List<EmployeeDto> actualEmployees1 = underTestService.getAllEmployees();

        assertAll(
                () -> assertNotNull(actualEmployees1),
                () -> assertEquals(actualEmployees1.size(), expectedEmployees1.size())
        );

        // 2 : getAllEmployees Success (Empty list but valid - from external API)
        EmployeeServiceDto expected2 = new EmployeeServiceDto();
        List<EmployeeDto> expectedEmployees2 = null;
        expected2.setData(expectedEmployees2);
        expected2.setStatus("Success");
        expected2.setMessage("All messages are retrieved");

        when(mockRestTemplate.getForObject(uri, EmployeeServiceDto.class)).thenReturn(expected2);
        List<EmployeeDto> actualEmployees2 = underTestService.getAllEmployees();

        assertAll(
                () -> assertNull(actualEmployees2)
        );

        // 3 : getAllEmployees Success (5xx response from external API, fetches the data from backupService)
        List<EmployeeDto> expectedEmployees3 = new ArrayList<>();
        expectedEmployees3.add(new EmployeeDto("1", "Tiger Nixon", "320800", "61", ""));
        expectedEmployees3.add(new EmployeeDto("2", "Garrett Winters", "170750", "63", ""));

        when(mockRestTemplate.getForObject(uri, EmployeeServiceDto.class)).thenThrow(HttpServerErrorException.class);
        when(mockBackupService.getAllEmployees()).thenReturn(expectedEmployees3);
        List<EmployeeDto> actualEmployees3 = underTestService.getAllEmployees();

        assertAll(
                () -> assertNotNull(actualEmployees3),
                () -> assertFalse(actualEmployees3.isEmpty())
        );
    }

    @Test
    void getEmployeesByNameSearch() throws URISyntaxException {

        URI uri = new URI(BASE_URL + "/api/v1/employees");

        // 1 : getEmployeesByNameSearch Failure (Name not valid)
        String name1 = "Tiger!";

        when(mockFieldsValidator.isValidAlphaNumericString(name1)).thenReturn(false);
        EmployeeFieldsNotValidException fieldsNotValidException = assertThrows(EmployeeFieldsNotValidException.class,
                () -> underTestService.getEmployeesByNameSearch(name1), "Name not valid!");

        assertNotNull(fieldsNotValidException.getMessage());

        // 2 : getEmployeesByNameSearch Success (Name found)
        String name2 = "tiger";
        EmployeeServiceDto expected2 = new EmployeeServiceDto();
        List<EmployeeDto> expectedEmployees2 = new ArrayList<>();
        expectedEmployees2.add(new EmployeeDto("1", "Tiger Nixon", "320800", "61", ""));
        expectedEmployees2.add(new EmployeeDto("2", "Garrett Winters", "170750", "63", ""));
        expected2.setData(expectedEmployees2);
        expected2.setStatus("Success");
        expected2.setMessage("All records retrieved");

        when(mockFieldsValidator.isValidAlphaNumericString(name2)).thenReturn(true);
        when(mockRestTemplate.getForObject(uri, EmployeeServiceDto.class)).thenReturn(expected2);
        List<EmployeeDto> actualEmployees2 = underTestService.getEmployeesByNameSearch(name2);

        assertAll(
                () -> assertNotNull(actualEmployees2),
                () -> assertFalse(actualEmployees2.isEmpty())
        );

        // 3 : getEmployeesByNameSearch Success (Name not found - empty list)
        String name3 = "xyz";
        EmployeeServiceDto expected3 = new EmployeeServiceDto();
        List<EmployeeDto> expectedEmployees3 = new ArrayList<>();
        expected3.setData(expectedEmployees3);
        expected3.setStatus("Success");
        expected3.setMessage("All records retrieved");

        when(mockFieldsValidator.isValidAlphaNumericString(name3)).thenReturn(true);
        when(mockRestTemplate.getForObject(uri, EmployeeServiceDto.class)).thenReturn(expected3);
        List<EmployeeDto> actualEmployees3 = underTestService.getEmployeesByNameSearch(name3);

        assertAll(
                () -> assertNotNull(actualEmployees3),
                () -> assertTrue(actualEmployees3.isEmpty())
        );

        // 4, 5 : getEmployeesByNameSearch Success, Success - 5xx error from external API (Name found - backupService, Name not found (empty list) - backupService)
        String name4 = "tiger";
        String name5 = "xyz";

        when(mockFieldsValidator.isValidAlphaNumericString(name4)).thenReturn(true);
        when(mockFieldsValidator.isValidAlphaNumericString(name5)).thenReturn(true);
        when(mockRestTemplate.getForObject(uri, EmployeeServiceDto.class)).thenThrow(HttpServerErrorException.class);

        List<EmployeeDto> expectedEmployees45 = new ArrayList<>();
        expectedEmployees45.add(new EmployeeDto("1", "Tiger Nixon", "320800", "61", ""));
        expectedEmployees45.add(new EmployeeDto("2", "Garrett Winters", "170750", "63", ""));

        when(mockBackupService.getAllEmployees()).thenReturn(expectedEmployees45);
        List<EmployeeDto> actualEmployees4 = underTestService.getEmployeesByNameSearch(name4);
        List<EmployeeDto> actualEmployees5 = underTestService.getEmployeesByNameSearch(name5);

        assertAll(
                () -> assertNotNull(actualEmployees4),
                () -> assertFalse(actualEmployees4.isEmpty()),
                () -> assertNotNull(actualEmployees5),
                () -> assertTrue(actualEmployees5.isEmpty())
        );
    }

    @Test
    void getEmployeeByIdSearch() throws URISyntaxException {

        String externalApiUri = BASE_URL + "/api/v1/employee/";

        // 1 : getEmployeeByIdSearch Failure (not valid id)
        String id1 = "abc";

        when(mockFieldsValidator.isValidNumber(id1)).thenReturn(false);
        EmployeeFieldsNotValidException fieldsNotValidException = assertThrows(EmployeeFieldsNotValidException.class,
                () -> underTestService.getEmployeeByIdSearch(id1), "Id not valid!");

        assertNotNull(fieldsNotValidException.getMessage());

        // 2 : getEmployeeByIdSearch Success (Employee found)
        String id2 = "1";
        EmployeeServiceDto expected2 = new EmployeeServiceDto();
        EmployeeDto expectedEmployee2 = new EmployeeDto("1", "Tiger", "", "", "");
        expected2.setData(expectedEmployee2);
        expected2.setStatus("Success");
        expected2.setMessage("All records retrieved!");

        when(mockFieldsValidator.isValidNumber(id2)).thenReturn(true);
        when(mockRestTemplate.getForObject(new URI(externalApiUri + id2), EmployeeServiceDto.class)).thenReturn(expected2);
        EmployeeDto actualEmployee2 = underTestService.getEmployeeByIdSearch(id2);

        assertAll(
                () -> assertNotNull(actualEmployee2),
                () -> assertEquals(actualEmployee2.getId(), id2)
        );

        // 3 : getEmployeeByIdSearch Success (Employee not found = returns null)
        String id3 = "1000000";
        EmployeeServiceDto expected3 = new EmployeeServiceDto();
        expected3.setData(null);
        expected3.setStatus("Success");
        expected3.setMessage("All records retrieved!");

        when(mockFieldsValidator.isValidNumber(id3)).thenReturn(true);
        when(mockRestTemplate.getForObject(new URI(externalApiUri + id3), EmployeeServiceDto.class)).thenReturn(expected3);
        EmployeeDto actualEmployee3 = underTestService.getEmployeeByIdSearch(id3);

        assertAll(
                () -> assertNull(actualEmployee3)
        );

        // 4,5 : getEmployeeByIdSearch Success, Success - 5xx error from external API (record found by backupService, record not found (empty) by backupService)
        String id4 = "1";
        String id5 = "1000000";

        EmployeeDto expected4 = new EmployeeDto("1", "Tiger", "", "", "");
        EmployeeDto expected5 = null;

        when(mockFieldsValidator.isValidNumber(id4)).thenReturn(true);
        when(mockFieldsValidator.isValidNumber(id5)).thenReturn(true);
        lenient().when(mockRestTemplate.getForObject(externalApiUri + id4, EmployeeServiceDto.class)).thenThrow(HttpServerErrorException.class);
        lenient().when(mockRestTemplate.getForObject(externalApiUri + id5, EmployeeServiceDto.class)).thenThrow(HttpServerErrorException.class);
        lenient().when(mockBackupService.getEmployeeByIdSearch(id4)).thenReturn(expected4);
        lenient().when(mockBackupService.getEmployeeByIdSearch(id5)).thenReturn(expected5);

        EmployeeDto actual4 = underTestService.getEmployeeByIdSearch(id4);
        EmployeeDto actual5 = underTestService.getEmployeeByIdSearch(id5);

        assertAll(
                () -> assertNotNull(actual4),
                () -> assertEquals(actual4.getId(), id4),
                () -> assertNull(actual5)
        );
    }

    @Test
    void getEmployeesBySalaryOrdering() throws URISyntaxException {

        URI uri = new URI(BASE_URL + "/api/v1/employees");

        // 1 :  getEmployeesBySalaryOrdering Success (Returns sorted list - external API)
        EmployeeServiceDto expected1 = new EmployeeServiceDto();
        List<EmployeeDto> expectedEmployees1 = new ArrayList<>();
        expectedEmployees1.add(new EmployeeDto("1", "Tiger Nixon", "32800", "61", ""));
        expectedEmployees1.add(new EmployeeDto("2", "Garrett Winters", "170750", "63", ""));
        expected1.setData(expectedEmployees1);
        expected1.setStatus("Success");
        expected1.setMessage("All records retrieved");

        when(mockRestTemplate.getForObject(uri, EmployeeServiceDto.class)).thenReturn(expected1);
        List<EmployeeDto> actualEmployees1 = underTestService.getEmployeesBySalaryOrdering(EmployeeService.SALARY_ORDERING.DESCENDING);

        assertAll(
                () -> assertNotNull(actualEmployees1),
                () -> assertTrue(actualEmployees1.get(0).getSalary().length() > actualEmployees1.get(1).getSalary().length())
        );

        // 2 :  getEmployeesBySalaryOrdering Success (Returns sorted list - backupService)
        EmployeeServiceDto expected2 = new EmployeeServiceDto();
        List<EmployeeDto> expectedEmployees2 = new ArrayList<>();
        expectedEmployees2.add(new EmployeeDto("1", "Tiger Nixon", "32800", "61", ""));
        expectedEmployees2.add(new EmployeeDto("2", "Garrett Winters", "170750", "63", ""));
        expected2.setData(expectedEmployees2);
        expected2.setStatus("Success");
        expected2.setMessage("All records retrieved");

        when(mockRestTemplate.getForObject(uri, EmployeeServiceDto.class)).thenThrow(HttpServerErrorException.class);
        lenient().when(mockBackupService.getAllEmployees()).thenReturn(expectedEmployees2);
        List<EmployeeDto> actualEmployees2 = underTestService.getEmployeesBySalaryOrdering(EmployeeService.SALARY_ORDERING.DESCENDING);

        assertAll(
                () -> assertNotNull(actualEmployees2),
                () -> assertTrue(actualEmployees2.get(0).getSalary().length() > actualEmployees2.get(1).getSalary().length())
        );
    }

    @Test
    void createNewEmployee() throws URISyntaxException {

        URI uri = new URI(BASE_URL + "/api/v1/create");

        // 1 : createNewEmployee Failure (Employee Fields not valid)
        EmployeeDto expected1 = new EmployeeDto("", "abc!", "2345", "35a", "");

        when(mockFieldsValidator.isValidEmployeeDto(expected1)).thenReturn(false);
        EmployeeFieldsNotValidException fieldsNotValidException = assertThrows(EmployeeFieldsNotValidException.class,
                () -> underTestService.createNewEmployee(expected1), "Employee fields not valid");

        assertNotNull(fieldsNotValidException.getMessage());

        // 2 : createNewEmployee Success (external API)
        EmployeeServiceDto expected2 = new EmployeeServiceDto();
        EmployeeDto expectedEmployee2 = new EmployeeDto("", "abc", "2345", "35", "");
        expected2.setData(expectedEmployee2);
        expected2.setStatus("Success");

        when(mockFieldsValidator.isValidEmployeeDto(expectedEmployee2)).thenReturn(true);
        when(mockRestTemplate.postForObject(uri, expectedEmployee2, EmployeeServiceDto.class)).thenReturn(expected2);
        EmployeeDto actualEmployee2 = underTestService.createNewEmployee(expectedEmployee2);

        assertAll(
                () -> assertNotNull(actualEmployee2),
                () -> assertEquals(actualEmployee2.getName(), expectedEmployee2.getName()),
                () -> assertEquals(actualEmployee2.getSalary(), expectedEmployee2.getSalary()),
                () -> assertEquals(actualEmployee2.getAge(), expectedEmployee2.getAge())
        );

        // 3 : createNewEmployee Success (backupService)
        EmployeeServiceDto expected3 = new EmployeeServiceDto();
        EmployeeDto expectedEmployee3 = new EmployeeDto("", "abcd", "2345", "35", "");
        expected3.setData(expectedEmployee3);
        expected3.setStatus("Success");

        when(mockFieldsValidator.isValidEmployeeDto(expectedEmployee3)).thenReturn(true);
        when(mockRestTemplate.postForObject(uri, expectedEmployee3, EmployeeServiceDto.class)).thenThrow(HttpServerErrorException.class);
        when(mockBackupService.createNewEmployee(expectedEmployee3)).thenReturn(expectedEmployee3);
        EmployeeDto actualEmployee3 = underTestService.createNewEmployee(expectedEmployee3);

        assertAll(
                () -> assertNotNull(actualEmployee3),
                () -> assertEquals(actualEmployee3.getName(), expectedEmployee3.getName()),
                () -> assertEquals(actualEmployee3.getSalary(), expectedEmployee3.getSalary()),
                () -> assertEquals(actualEmployee3.getAge(), expectedEmployee3.getAge())
        );
    }

    @Test
    void deleteEmployeeById() throws URISyntaxException {

        String externalApiUri = BASE_URL + "/api/v1/delete/";

        // 1 : deleteEmployeeById Failure (Not valid id)
        String id1 = "35a";

        when(mockFieldsValidator.isValidNumber(id1)).thenReturn(false);
        EmployeeFieldsNotValidException fieldsNotValidException = assertThrows(EmployeeFieldsNotValidException.class,
                () -> underTestService.deleteEmployeeById(id1), "Id not valid!");

        assertNotNull(fieldsNotValidException.getMessage());

        // 2 : deleteEmployeeById (external API)
        String id2 = "1";

        when(mockFieldsValidator.isValidNumber(id2)).thenReturn(true);
        doNothing().when(mockRestTemplate).delete(new URI(externalApiUri + id2));
        underTestService.deleteEmployeeById(id2);

        // 3 : deleteEmployeeById (backupService)
        String id3 = "2";

        when(mockFieldsValidator.isValidNumber(id3)).thenReturn(true);
        doThrow(HttpServerErrorException.class).when(mockRestTemplate).delete(new URI (externalApiUri + id3));
        underTestService.deleteEmployeeById(id3);
    }
}