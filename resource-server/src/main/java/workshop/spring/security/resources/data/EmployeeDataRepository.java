package workshop.spring.security.resources.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDataRepository extends JpaRepository<Employee, Long> {

}
