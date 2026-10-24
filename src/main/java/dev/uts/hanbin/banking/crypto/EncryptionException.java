package dev.uts.hanbin.banking.crypto;

/** Raised when a field cannot be encrypted or decrypted; never carries plaintext or key material. */
public class EncryptionException extends RuntimeException {

  public EncryptionException(String message, Throwable cause) {
    super(message, cause);
  }

  public EncryptionException(String message) {
    super(message);
  }
}