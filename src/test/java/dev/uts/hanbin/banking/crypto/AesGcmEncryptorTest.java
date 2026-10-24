package dev.uts.hanbin.banking.crypto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Base64;
import org.junit.jupiter.api.Test;

class AesGcmEncryptorTest {

  private static final String KEY = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

  private final AesGcmEncryptor encryptor = new AesGcmEncryptor(KEY);

  @Test
  void roundTripsPlaintext() {
    String ciphertext = encryptor.encrypt("AU0012345678");

    assertThat(ciphertext).startsWith("v1:").doesNotContain("AU0012345678");
    assertThat(encryptor.decrypt(ciphertext)).isEqualTo("AU0012345678");
  }

  @Test
  void producesDifferentCiphertextForTheSamePlaintext() {
    // A fresh IV per call; equal plaintexts must not be linkable by looking at the column.
    assertThat(encryptor.encrypt("same")).isNotEqualTo(encryptor.encrypt("same"));
  }

  @Test
  void passesNullThrough() {
    assertThat(encryptor.encrypt(null)).isNull();
    assertThat(encryptor.decrypt(null)).isNull();
  }

  @Test
  void rejectsTamperedCiphertext() {
    byte[] payload =
        Base64.getDecoder().decode(encryptor.encrypt("AU0012345678").substring("v1:".length()));
    payload[payload.length - 1] ^= 0x01; // flip one bit of the GCM tag

    String tampered = "v1:" + Base64.getEncoder().encodeToString(payload);

    assertThatThrownBy(() -> encryptor.decrypt(tampered))
        .isInstanceOf(EncryptionException.class);
  }

  @Test
  void rejectsCiphertextFromAnotherKey() {
    AesGcmEncryptor other =
        new AesGcmEncryptor(Base64.getEncoder().encodeToString(new byte[32]));

    assertThatThrownBy(() -> encryptor.decrypt(other.encrypt("AU0012345678")))
        .isInstanceOf(EncryptionException.class);
  }

  @Test
  void rejectsValueWithoutVersionPrefix() {
    assertThatThrownBy(() -> encryptor.decrypt("AU0012345678"))
        .isInstanceOf(EncryptionException.class);
  }

  @Test
  void rejectsKeyThatIsNotAes256() {
    String shortKey = Base64.getEncoder().encodeToString(new byte[16]);

    assertThatThrownBy(() -> new AesGcmEncryptor(shortKey))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("32 bytes");
  }

  @Test
  void rejectsMissingKey() {
    assertThatThrownBy(() -> new AesGcmEncryptor(""))
        .isInstanceOf(IllegalStateException.class);
  }
}
