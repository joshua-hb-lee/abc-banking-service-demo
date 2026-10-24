package dev.uts.hanbin.banking.dto;

import dev.uts.hanbin.banking.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** Request body for recording a transaction against a customer. */
public record TransactionRequest(

    @NotNull(message = "transactionType is required")
    TransactionType transactionType,

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    @Digits(integer = 17, fraction = 2, message = "amount must have at most 2 decimal places")
    BigDecimal amount,

    @NotBlank(message = "currency is required")
    @Pattern(regexp = "[A-Z]{3}", message = "currency must be a 3-letter ISO-4217 code")
    String currency,

    @NotBlank(message = "counterpartyName is required")
    @Size(max = 100, message = "counterpartyName must be at most 100 characters")
    String counterpartyName,

    @NotBlank(message = "counterpartyAccount is required")
    @Size(max = 34, message = "counterpartyAccount must be at most 34 characters")
    @Pattern(regexp = "[A-Za-z0-9]+", message = "counterpartyAccount must be alphanumeric")
    String counterpartyAccount,

    @Size(max = 255, message = "memo must be at most 255 characters")
    String memo
) {

}
