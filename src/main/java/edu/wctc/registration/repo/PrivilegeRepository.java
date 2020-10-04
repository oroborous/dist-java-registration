package edu.wctc.registration.repo;

import edu.wctc.registration.repo.entity.Privilege;
import org.springframework.data.repository.CrudRepository;

public interface PrivilegeRepository extends CrudRepository<Privilege, Long> {
    Privilege findByName(String name);
}
