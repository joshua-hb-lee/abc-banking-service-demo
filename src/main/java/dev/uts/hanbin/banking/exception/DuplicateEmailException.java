package dev.uts.hanbin.banking.exception;

public class DuplicateEmailException extends RuntimeException {

  public DuplicateEmailException(String email) {
    super("Email already in use: " + email);
  }
}
