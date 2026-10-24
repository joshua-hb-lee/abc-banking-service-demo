package dev.uts.hanbin.banking.repository;

import dev.uts.hanbin.banking.entity.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

  boolean existsByEmail(String email);

  Optional<Customer> findByEmail(String email);
}
