package dev.uts.hanbin.banking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request body for creating and updating a customer. */
public record CustomerRequest(

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    String name,

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid address")
    @Size(max = 150, message = "email must be at most 150 characters")
    String email,

    @Size(max = 20, message = "phone must be at most 20 characters")
    String phone
) {

}
