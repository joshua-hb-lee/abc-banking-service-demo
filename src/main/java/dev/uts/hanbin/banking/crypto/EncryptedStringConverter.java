package dev.uts.hanbin.banking.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Encrypts a {@code String} field on the way into the database and decrypts it on the way out, so
 * the entity model stays plaintext while the column only ever holds ciphertext.
 *
 * <p>Applied explicitly with {@code @Convert} rather than {@code autoApply}, so adding a new
 * String column never encrypts it by accident. Hibernate resolves this converter through Spring's
 * bean container, which is how it receives {@link AesGcmEncryptor}.
 *
 * <p>Trade-off worth knowing: a random IV means the same plaintext encrypts differently every
 * time, so encrypted columns cannot be searched, sorted, or made unique by the database.
 */
@Component
@Converter
@RequiredArgsConstructor
public class EncryptedStringConverter implements AttributeConverter<String, String> {

  private final AesGcmEncryptor encryptor;

  @Override
  public String convertToDatabaseColumn(String attribute) {
    return encryptor.encrypt(attribute);
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    return encryptor.decrypt(dbData);
  }
}