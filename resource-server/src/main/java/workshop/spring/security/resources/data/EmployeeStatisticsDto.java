package workshop.spring.security.resources.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class EmployeeStatisticsDto {

    private final int numberOfEmployees;

    private final int averageSalary;

    private final String highestPaidEmployee;

    @JsonCreator
    public EmployeeStatisticsDto(@JsonProperty("numberOfEmployees") int numberOfEmployees,
                                 @JsonProperty("averageSalary") int averageSalary,
                                 @JsonProperty("highestPaidEmployee") String highestPaidEmployee) {
        this.numberOfEmployees = numberOfEmployees;
        this.averageSalary = averageSalary;
        this.highestPaidEmployee = highestPaidEmployee;
    }

    public int getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public int getAverageSalary() {
        return averageSalary;
    }

    public String getHighestPaidEmployee() {
        return highestPaidEmployee;
    }
}
