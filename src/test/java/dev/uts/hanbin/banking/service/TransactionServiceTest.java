package dev.uts.hanbin.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.uts.hanbin.banking.dto.CustomerRequest;
import dev.uts.hanbin.banking.dto.TransactionRequest;
import dev.uts.hanbin.banking.dto.TransactionResponse;
import dev.uts.hanbin.banking.entity.TransactionType;
import dev.uts.hanbin.banking.exception.CustomerNotFoundException;
import dev.uts.hanbin.banking.exception.TransactionNotFoundException;
import dev.uts.hanbin.banking.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TransactionServiceTest {

  private static final String ACCOUNT = "AU0012345678";
  private static final String MEMO = "Rent for October";

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private CustomerService customerService;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private EntityManager entityManager;

  private static TransactionRequest request() {
    return new TransactionRequest(TransactionType.TRANSFER, new BigDecimal("250.75"), "AUD",
        "Jane Landlord", ACCOUNT, MEMO);
  }

  private Long createCustomer(String email) {
    return customerService.create(new CustomerRequest("Hanbin Lee", email, "0400000000")).id();
  }

  @Test
  void createsAndReadsBackTransaction() {
    Long customerId = createCustomer("tx-create@abc.com");

    TransactionResponse created = transactionService.create(customerId, request());

    assertThat(created.id()).isNotNull();
    assertThat(created.reference()).isNotBlank();
    assertThat(created.amount()).isEqualByComparingTo("250.75");
    assertThat(created.memo()).isEqualTo(MEMO);
    assertThat(transactionService.findById(created.id()).counterpartyName())
        .isEqualTo("Jane Landlord");
  }

  @Test
  void storesSensitiveColumnsAsCiphertext() {
    Long customerId = createCustomer("tx-cipher@abc.com");
    Long id = transactionService.create(customerId, request()).id();
    entityManager.flush();
    entityManager.clear();

    Object[] stored = (Object[]) entityManager
        .createNativeQuery(
            "select counterparty_account, counterparty_name, memo from transactions where id = ?1")
        .setParameter(1, id)
        .getSingleResult();

    // What actually landed in the table is the AES-GCM envelope, not the values we passed in.
    assertThat((String) stored[0]).startsWith("v1:").doesNotContain(ACCOUNT);
    assertThat((String) stored[1]).startsWith("v1:").doesNotContain("Jane Landlord");
    assertThat((String) stored[2]).startsWith("v1:").doesNotContain(MEMO);

    // ...and reading through JPA transparently decrypts it again.
    assertThat(transactionService.findById(id).memo()).isEqualTo(MEMO);
  }

  @Test
  void masksAccountNumberInResponses() {
    Long customerId = createCustomer("tx-mask@abc.com");

    TransactionResponse created = transactionService.create(customerId, request());

    assertThat(created.counterpartyAccountMasked()).isEqualTo("********5678");
  }

  @Test
  void keepsAmountQueryableInPlaintext() {
    Long customerId = createCustomer("tx-amount@abc.com");
    Long id = transactionService.create(customerId, request()).id();
    entityManager.flush();

    Object amount = entityManager
        .createNativeQuery("select amount from transactions where id = ?1")
        .setParameter(1, id)
        .getSingleResult();

    assertThat(new BigDecimal(amount.toString())).isEqualByComparingTo("250.75");
  }

  @Test
  void listsTransactionsForCustomer() {
    Long customerId = createCustomer("tx-list@abc.com");
    transactionService.create(customerId, request());
    transactionService.create(customerId, request());

    assertThat(transactionService.findByCustomer(customerId)).hasSize(2);
  }

  @Test
  void findsTransactionByReference() {
    Long customerId = createCustomer("tx-ref@abc.com");
    String reference = transactionService.create(customerId, request()).reference();

    assertThat(transactionService.findByReference(reference).counterpartyName())
        .isEqualTo("Jane Landlord");
  }

  @Test
  void deletingCustomerCascadesToTransactions() {
    Long customerId = createCustomer("tx-cascade@abc.com");
    transactionService.create(customerId, request());

    customerService.delete(customerId);
    entityManager.flush();

    assertThat(customerRepository.findById(customerId)).isEmpty();
    assertThat(entityManager
        .createNativeQuery("select count(*) from transactions where customer_id = ?1")
        .setParameter(1, customerId)
        .getSingleResult()
        .toString()).isEqualTo("0");
  }

  @Test
  void throwsWhenCustomerMissing() {
    assertThatThrownBy(() -> transactionService.create(-1L, request()))
        .isInstanceOf(CustomerNotFoundException.class);
  }

  @Test
  void throwsWhenTransactionMissing() {
    assertThatThrownBy(() -> transactionService.findById(-1L))
        .isInstanceOf(TransactionNotFoundException.class);
  }
}
