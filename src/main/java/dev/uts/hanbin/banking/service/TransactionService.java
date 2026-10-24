package dev.uts.hanbin.banking.service;

import dev.uts.hanbin.banking.dto.TransactionRequest;
import dev.uts.hanbin.banking.dto.TransactionResponse;
import dev.uts.hanbin.banking.entity.Customer;
import dev.uts.hanbin.banking.entity.Transaction;
import dev.uts.hanbin.banking.exception.CustomerNotFoundException;
import dev.uts.hanbin.banking.exception.TransactionNotFoundException;
import dev.uts.hanbin.banking.repository.CustomerRepository;
import dev.uts.hanbin.banking.repository.TransactionRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transaction use cases.
 *
 * <p>Nothing here encrypts or decrypts explicitly — the AES-GCM converter on the entity does that
 * as JPA writes and reads the rows, so the service only ever handles plaintext.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final CustomerRepository customerRepository;

  @Transactional
  public TransactionResponse create(Long customerId, TransactionRequest request) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException(customerId));

    Transaction transaction = Transaction.builder()
        .reference(UUID.randomUUID().toString())
        .transactionType(request.transactionType())
        .amount(request.amount())
        .currency(request.currency())
        .counterpartyName(request.counterpartyName())
        .counterpartyAccount(request.counterpartyAccount())
        .memo(request.memo())
        .build();
    customer.addTransaction(transaction);

    return TransactionResponse.from(transactionRepository.save(transaction));
  }

  public List<TransactionResponse> findByCustomer(Long customerId) {
    if (!customerRepository.existsById(customerId)) {
      throw new CustomerNotFoundException(customerId);
    }

    return transactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
        .map(TransactionResponse::from)
        .toList();
  }

  public TransactionResponse findById(Long id) {
    return TransactionResponse.from(transactionRepository.findById(id)
        .orElseThrow(() -> new TransactionNotFoundException(id)));
  }

  public TransactionResponse findByReference(String reference) {
    return TransactionResponse.from(transactionRepository.findByReference(reference)
        .orElseThrow(() -> new TransactionNotFoundException(reference)));
  }

  @Transactional
  public void delete(Long id) {
    Transaction transaction = transactionRepository.findById(id)
        .orElseThrow(() -> new TransactionNotFoundException(id));

    transactionRepository.delete(transaction);
  }
}
