package workshop.spring.security.resources.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDto {

    @JsonView(EmployeeViews.Public.class)
    private final Long id;

    @JsonView(EmployeeViews.Public.class)
    private final String codeName;

    @JsonView(EmployeeViews.Internal.class)
    private final String fullName;

    @JsonView(EmployeeViews.Accounting.class)
    private final Integer salary;

    @JsonView(EmployeeViews.Management.class)
    private final String performanceRating;


    @JsonCreator
    public EmployeeDto(@JsonProperty("id") Long id,
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

    public Long getId() {
        return id;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getFullName() {
        return fullName;
    }

    public Integer getSalary() {
        return salary;
    }

    public String getPerformanceRating() {
        return performanceRating;
    }

    public static EmployeeDto fromEmployee(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getCodeName(),
                employee.getFullName(),
                employee.getSalary() == 0 ? null : employee.getSalary(),
                employee.getPerformanceRating().name()
        );
    }
}
