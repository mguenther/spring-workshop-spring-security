package workshop.spring.security.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class EmployeeViewController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeViewController.class);

    private final RestTemplate client;

    public EmployeeViewController(RestTemplate client) {
        this.client = client;
    }

    @GetMapping(value = "/")
    public String emailModel(Model model, Authentication authentication) {
        try {
            var statisticsResponse = client.getForEntity("/employee/statistics", EmployeeStatisticsDto.class);
            model.addAttribute("statistics", statisticsResponse.getBody());
        } catch (HttpClientErrorException ex ) {
            log.warn("Exception while fetching statistics with status code '{}' and message '{}'", ex.getStatusCode().value(), ex.getCause(), ex);
        }

        try {
            var employeeResponse = client.getForEntity("/employee", EmployeeDto[].class);
            model.addAttribute("employees", employeeResponse.getBody());
        } catch (HttpClientErrorException ex ) {
            log.warn("Exception while fetching statistics with status code '{}' and message '{}'", ex.getStatusCode().value(), ex.getCause(), ex);
            model.addAttribute("employees", null);
        }

        if (authentication != null && authentication.getPrincipal() != null) {
            model.addAttribute("principal", authentication.getPrincipal());
        }

        return "employee";
    }


     record EmployeeDto(Long id, String codeName, String fullName, Integer salary, String performanceRating) {

        @JsonCreator
        EmployeeDto(@JsonProperty("id") Long id,
                    @JsonProperty("codeName") String codeName,
                    @JsonProperty("fullName") String fullName,
                    @JsonProperty("salary") Integer salary,
                    @JsonProperty("performanceRating") String performanceRating) {
            this.id = id;
            this.codeName = codeName;
            this.fullName = fullName;
            this.salary = salary;
            this.performanceRating = performanceRating;
        }
     }

    record EmployeeStatisticsDto(int numberOfEmployees, int averageSalary, String highestPaidEmployee) {

        @JsonCreator
        public EmployeeStatisticsDto(@JsonProperty("numberOfEmployees") int numberOfEmployees,
                                     @JsonProperty("averageSalary") int averageSalary,
                                     @JsonProperty("highestPaidEmployee") String highestPaidEmployee) {
            this.numberOfEmployees = numberOfEmployees;
            this.averageSalary = averageSalary;
            this.highestPaidEmployee = highestPaidEmployee;
        }


    }
}

