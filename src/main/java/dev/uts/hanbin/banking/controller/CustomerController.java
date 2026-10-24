package dev.uts.hanbin.banking.controller;

import dev.uts.hanbin.banking.dto.CustomerRequest;
import dev.uts.hanbin.banking.dto.CustomerResponse;
import dev.uts.hanbin.banking.service.CustomerService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService customerService;

  @PostMapping
  public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
    CustomerResponse created = customerService.create(request);
    return ResponseEntity.created(URI.create("/api/customers/" + created.id())).body(created);
  }

  @GetMapping
  public ResponseEntity<List<CustomerResponse>> findAll() {
    return ResponseEntity.ok(customerService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(customerService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CustomerResponse> update(@PathVariable Long id,
      @Valid @RequestBody CustomerRequest request) {
    return ResponseEntity.ok(customerService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    customerService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
