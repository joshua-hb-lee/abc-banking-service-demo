package dev.uts.hanbin.banking.exception;

public class TransactionNotFoundException extends RuntimeException {

  public TransactionNotFoundException(Long id) {
    super("Transaction not found: " + id);
  }

  public TransactionNotFoundException(String reference) {
    super("Transaction not found: " + reference);
  }
}
