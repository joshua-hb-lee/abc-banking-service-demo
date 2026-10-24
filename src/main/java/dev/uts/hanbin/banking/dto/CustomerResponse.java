package dev.uts.hanbin.banking.dto;

import dev.uts.hanbin.banking.entity.Customer;
import java.time.LocalDateTime;

/** Response body returned by the customer endpoints. */
public record CustomerResponse(
    Long id,
    String name,
    String email,
    String phone,
    LocalDateTime createdAt
) {

  public static CustomerResponse from(Customer customer) {
    return new CustomerResponse(
        customer.getId(),
        customer.getName(),
        customer.getEmail(),
        customer.getPhone(),
        customer.getCreatedAt());
  }
}
