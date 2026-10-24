package dev.uts.hanbin.banking.dto;

import dev.uts.hanbin.banking.entity.Transaction;
import dev.uts.hanbin.banking.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response body returned by the transaction endpoints.
 *
 * <p>Encryption protects the account number at rest; masking keeps it out of API responses, logs,
 * and browser history as well. The last four digits are enough for a customer to recognise the
 * account, and producing them at all proves the ciphertext was decrypted on read.
 */
public record TransactionResponse(
    Long id,
    String reference,
    TransactionType transactionType,
    BigDecimal amount,
    String currency,
    String counterpartyName,
    String counterpartyAccountMasked,
    String memo,
    LocalDateTime createdAt
) {

  private static final int VISIBLE_SUFFIX_LENGTH = 4;

  public static TransactionResponse from(Transaction transaction) {
    return new TransactionResponse(
        transaction.getId(),
        transaction.getReference(),
        transaction.getTransactionType(),
        transaction.getAmount(),
        transaction.getCurrency(),
        transaction.getCounterpartyName(),
        mask(transaction.getCounterpartyAccount()),
        transaction.getMemo(),
        transaction.getCreatedAt());
  }

  private static String mask(String account) {
    if (account == null) {
      return null;
    }
    if (account.length() <= VISIBLE_SUFFIX_LENGTH) {
      return "*".repeat(account.length());
    }

    int hiddenLength = account.length() - VISIBLE_SUFFIX_LENGTH;
    return "*".repeat(hiddenLength) + account.substring(hiddenLength);
  }
}
