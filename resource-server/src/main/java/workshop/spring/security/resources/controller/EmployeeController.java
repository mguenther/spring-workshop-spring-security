package workshop.spring.security.resources.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import workshop.spring.security.resources.data.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.MapperFeature.DEFAULT_VIEW_INCLUSION;

@RestController
@RequestMapping(path = "employee")
public class EmployeeController {

    private final EmployeeDataRepository repo;

    private final ObjectMapper mapper = JsonMapper.builder().disable(DEFAULT_VIEW_INCLUSION).build();

    public EmployeeController(EmployeeDataRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = "/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EmployeeDto getEmployee(@PathVariable("employeeId") Long employeeId, Authentication authentication) {
        var employee = getEmployeeForId(employeeId);
        return employeeToRoleBasedView(employee, authentication);

    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EmployeeDto> getEmployees(Authentication authentication) {
        return repo.findAll()
                .stream()
                .map(employee -> employeeToRoleBasedView(employee, authentication))
                .collect(Collectors.toList());
    }


    @GetMapping(path = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('MANAGEMENT')")
    public EmployeeStatisticsDto getEmployeeStatistics() {
        var employees = repo.findAll();
        var count = employees.size();
        var averageSalary = (int) Math.floor(employees.stream().mapToInt(Employee::getSalary).average().orElseThrow());
        var highestPayingemployee = employees.stream().max(Comparator.comparingInt(Employee::getSalary)).orElseThrow();

        return new EmployeeStatisticsDto(count, averageSalary, highestPayingemployee.getFullName());
    }



    private Employee getEmployeeForId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private EmployeeDto employeeToRoleBasedView(Employee employee, Authentication authentication) {

        try {
            var dto = EmployeeDto.fromEmployee(employee);
            var serialized = "{}";

            if (authentication == null || authentication.getAuthorities().isEmpty()) {
                serialized = mapper.writerWithView(EmployeeViews.Public.class)
                        .writeValueAsString(dto);
            } else if (containsAuthority(authentication.getAuthorities(), "MANAGEMENT")) {
                serialized = mapper.writerWithView(EmployeeViews.Management.class)
                        .writeValueAsString(dto);
            } else if (containsAuthority(authentication.getAuthorities(), "ACCOUNTING")) {
                serialized = mapper.writerWithView(EmployeeViews.Accounting.class)
                        .writeValueAsString(dto);
            } else if (authentication.isAuthenticated()){
                serialized = mapper.writerWithView(EmployeeViews.Internal.class)
                        .writeValueAsString(dto);
            } else {
                serialized = mapper.writerWithView(EmployeeViews.Public.class)
                        .writeValueAsString(dto);
            }

            return mapper.readValue(serialized, EmployeeDto.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while serializing employee");
        }

    }


    private boolean containsAuthority(Collection<? extends GrantedAuthority> authorities, String requestedAuthority) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(it -> it.equals(requestedAuthority));
    }

}
