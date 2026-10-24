package dev.uts.hanbin.banking.entity;

import dev.uts.hanbin.banking.crypto.EncryptedStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

/**
 * A single money movement on a customer's account.
 *
 * <p>The counterparty details and the free-text memo are personal/financial identifiers, so they
 * are encrypted with AES-256-GCM before they reach the database. Everything the service needs to
 * query on — amount, currency, type, timestamps — stays in plaintext, because a randomised-IV
 * ciphertext cannot be filtered, sorted, or aggregated by the database.
 */
@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  /** Opaque public identifier, safe to hand out in URLs and receipts. */
  @Column(nullable = false, unique = true, length = 36)
  private String reference;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false, length = 20)
  private TransactionType transactionType;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency;

  // --- Encrypted at rest; the column holds "v1:<base64>", never the value below. ---

  @Convert(converter = EncryptedStringConverter.class)
  @Column(name = "counterparty_name", nullable = false, length = 255)
  private String counterpartyName;

  @Convert(converter = EncryptedStringConverter.class)
  @Column(name = "counterparty_account", nullable = false, length = 255)
  private String counterpartyAccount;

  @Convert(converter = EncryptedStringConverter.class)
  @Column(length = 512)
  private String memo;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Builder
  public Transaction(String reference, TransactionType transactionType, BigDecimal amount,
      String currency, String counterpartyName, String counterpartyAccount, String memo) {
    this.reference = reference;
    this.transactionType = transactionType;
    this.amount = amount;
    this.currency = currency;
    this.counterpartyName = counterpartyName;
    this.counterpartyAccount = counterpartyAccount;
    this.memo = memo;
  }

  void assignCustomer(Customer customer) {
    this.customer = customer;
  }
}