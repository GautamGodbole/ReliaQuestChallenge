**Current API endpoints**
(BASE URL : http://localhost:8080/api/v1/employees)

1. getAllEmployees() : /
2. getEmployeesByNameSearch() : /search/{searchString}
3. getEmployeeByIdSearch() : /{id}
4. getHighestSalaryOfEmployees() : /highestSalary
5. getTopTenHighestEarningEmployeeNames() : /topTenHighestEarningEmployeeNames
6. createEmployee() : /
7. deleteEmployeeById : /{id}

I believe the first 5 endpoints should be clubbed into a single endpoint
Alternatives :
1. BASE_URL
2. BASE_URL?name={searchString}
3. BASE_URL?id={id}
4. BASE_URL?orderBy=salary&direction=descending&limit=1
5. BASE_URL?orderBy=salary&direction=descending&limit=10

This is because all the first 5 request are just for display/order/filter purpose (GET).
So, I believe keeping only one endpoint would better.


**Observation regarding external API**
(BASE_UTL :  https://dummy.restapiexample.com/)
1. Rate limiting has been implemented on the remote side to allow only 1 request per minute.
2. Remote side has kept the static list of 24 employees.
3. Add/Delete does not happen actually at the remote side (just mimicking add/delete functionality).
4. If the Search-Id is not in 1-24 range, external API still responds with 200 OK (but with null record) - same goes for add/delete, no validations implemented.