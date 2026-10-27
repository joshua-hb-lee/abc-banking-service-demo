# ABC Banking Service Demo

## Part 3. Data Protection and Lifecycle

<img width="762" height="552" alt="Image" src="https://github.com/user-attachments/assets/5f370022-8f38-4c5e-937b-7b73c1b46317" />

This project was focusing on demonstrating the data protection in terms of encryption and
decryption.

Additionally, it's also configured AWS Secrets Manager to securely get sensitive information for the
application configuration.

- [`DDL.md`](https://github.com/joshua-hb-lee/abc-banking-service-demo/blob/main/DDL.md): contains the database schema and tables
- [`AesGcmEncryptor`](https://github.com/joshua-hb-lee/abc-banking-service-demo/blob/main/src/main/java/dev/uts/hanbin/banking/crypto/AesGcmEncryptor.java): encryption and decryption with AES-GCM algorithm (symmetric)
