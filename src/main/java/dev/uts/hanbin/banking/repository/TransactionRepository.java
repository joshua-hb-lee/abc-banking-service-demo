package dev.uts.hanbin.banking.repository;

import dev.uts.hanbin.banking.entity.Transaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  // Only plaintext columns can appear in a derived query; the encrypted ones are opaque to SQL.
  List<Transaction> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

  Optional<Transaction> findByReference(String reference);
}