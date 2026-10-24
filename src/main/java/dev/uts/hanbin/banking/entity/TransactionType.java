package dev.uts.hanbin.banking.entity;

/** Direction and purpose of a money movement, stored as its name in {@code transaction_type}. */
public enum TransactionType {
  DEPOSIT,
  WITHDRAWAL,
  TRANSFER,
  PAYMENT
}