package edu.wctc.registration.repo;

import edu.wctc.registration.repo.entity.VerificationToken;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
