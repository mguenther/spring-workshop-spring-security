package workshop.spring.security.resources.data;

import jakarta.persistence.*;

@Entity
@Table(name = "EMPLOYEE")
public class Employee {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "seq_PK", sequenceName = "SEQ_PRIMARYKEY", allocationSize = 1, initialValue = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_PK")
    private Long id;

    @Column(name = "CODE_NAME")
    private String codeName;

    @Column(name = "NAME")
    private String fullName;

    @Column(name = "SALARY")
    private int salary;

    @Enumerated(EnumType.STRING)
    @Column(name = "RATING")
    private PerformanceRating performanceRating;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public PerformanceRating getPerformanceRating() {
        return performanceRating;
    }

    public void setPerformanceRating(PerformanceRating performanceRating) {
        this.performanceRating = performanceRating;
    }
}
