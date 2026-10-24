package dev.uts.hanbin.banking.controller;

import dev.uts.hanbin.banking.dto.TransactionRequest;
import dev.uts.hanbin.banking.dto.TransactionResponse;
import dev.uts.hanbin.banking.service.TransactionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping("/customers/{customerId}/transactions")
  public ResponseEntity<TransactionResponse> create(@PathVariable Long customerId,
      @Valid @RequestBody TransactionRequest request) {
    TransactionResponse created = transactionService.create(customerId, request);
    return ResponseEntity.created(URI.create("/api/transactions/" + created.id())).body(created);
  }

  @GetMapping("/customers/{customerId}/transactions")
  public ResponseEntity<List<TransactionResponse>> findByCustomer(@PathVariable Long customerId) {
    return ResponseEntity.ok(transactionService.findByCustomer(customerId));
  }

  @GetMapping("/transactions/{id}")
  public ResponseEntity<TransactionResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(transactionService.findById(id));
  }

  @GetMapping("/transactions/reference/{reference}")
  public ResponseEntity<TransactionResponse> findByReference(@PathVariable String reference) {
    return ResponseEntity.ok(transactionService.findByReference(reference));
  }

  @DeleteMapping("/transactions/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    transactionService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
