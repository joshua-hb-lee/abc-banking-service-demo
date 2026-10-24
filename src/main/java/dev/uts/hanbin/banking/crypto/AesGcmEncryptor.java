package dev.uts.hanbin.banking.crypto;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Application-level AES-256-GCM encryption for sensitive columns.
 *
 * <p>GCM is an AEAD mode, so a tampered ciphertext fails to decrypt instead of silently returning
 * corrupted data. A fresh 96-bit IV is drawn per encryption, which is what keeps GCM safe: reusing
 * an IV under the same key leaks plaintext and the authentication key.
 *
 * <p>Wire format: {@code v1:} + base64(iv ‖ ciphertext ‖ tag). The version prefix exists so a
 * future key rotation or algorithm change can be recognised on read.
 */
@Component
public class AesGcmEncryptor {

  static final String VERSION_PREFIX = "v1:";

  private static final String TRANSFORMATION = "AES/GCM/NoPadding";
  private static final int KEY_LENGTH_BYTES = 32; // AES-256
  private static final int IV_LENGTH_BYTES = 12; // 96-bit IV, the GCM-recommended size
  private static final int TAG_LENGTH_BITS = 128;

  private final SecureRandom secureRandom = new SecureRandom();
  private final SecretKey key;

  public AesGcmEncryptor(@Value("${banking.security.encryption.aes-key:}") String base64Key) {
    this.key = loadKey(base64Key);
  }

  private static SecretKey loadKey(String base64Key) {
    if (base64Key == null || base64Key.isBlank()) {
      throw new IllegalStateException(
          "banking.security.encryption.aes-key is not set; supply a base64-encoded 256-bit key "
              + "(for example from AWS Secrets Manager) before starting the application");
    }

    byte[] keyBytes;
    try {
      keyBytes = Base64.getDecoder().decode(base64Key.trim());
    } catch (IllegalArgumentException ex) {
      throw new IllegalStateException(
          "banking.security.encryption.aes-key must be valid base64", ex);
    }

    if (keyBytes.length != KEY_LENGTH_BYTES) {
      throw new IllegalStateException(
          "banking.security.encryption.aes-key must decode to " + KEY_LENGTH_BYTES
              + " bytes (AES-256) but was " + keyBytes.length);
    }

    return new SecretKeySpec(keyBytes, "AES");
  }

  /** Encrypts UTF-8 plaintext; {@code null} passes through so nullable columns stay nullable. */
  public String encrypt(String plaintext) {
    if (plaintext == null) {
      return null;
    }

    byte[] iv = new byte[IV_LENGTH_BYTES];
    secureRandom.nextBytes(iv);

    try {
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
      byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

      byte[] payload = new byte[iv.length + ciphertext.length];
      System.arraycopy(iv, 0, payload, 0, iv.length);
      System.arraycopy(ciphertext, 0, payload, iv.length, ciphertext.length);

      return VERSION_PREFIX + Base64.getEncoder().encodeToString(payload);
    } catch (Exception ex) {
      throw new EncryptionException("Unable to encrypt value", ex);
    }
  }

  /** Reverses {@link #encrypt}; throws if the stored value was truncated or tampered with. */
  public String decrypt(String stored) {
    if (stored == null) {
      return null;
    }
    if (!stored.startsWith(VERSION_PREFIX)) {
      throw new EncryptionException("Stored value is not in the expected " + VERSION_PREFIX
          + " envelope; it may predate encryption or have been written outside the application");
    }

    byte[] payload;
    try {
      payload = Base64.getDecoder().decode(stored.substring(VERSION_PREFIX.length()));
    } catch (IllegalArgumentException ex) {
      throw new EncryptionException("Stored value is not valid base64", ex);
    }

    if (payload.length <= IV_LENGTH_BYTES) {
      throw new EncryptionException("Stored value is too short to contain an IV and a GCM tag");
    }

    try {
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.DECRYPT_MODE, key,
          new GCMParameterSpec(TAG_LENGTH_BITS, payload, 0, IV_LENGTH_BYTES));
      byte[] plaintext =
          cipher.doFinal(payload, IV_LENGTH_BYTES, payload.length - IV_LENGTH_BYTES);

      return new String(plaintext, StandardCharsets.UTF_8);
    } catch (Exception ex) {
      throw new EncryptionException("Unable to decrypt value; the GCM tag did not verify", ex);
    }
  }
}