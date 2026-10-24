package dev.uts.hanbin.banking.service;

import dev.uts.hanbin.banking.dto.CustomerRequest;
import dev.uts.hanbin.banking.dto.CustomerResponse;
import dev.uts.hanbin.banking.entity.Customer;
import dev.uts.hanbin.banking.exception.CustomerNotFoundException;
import dev.uts.hanbin.banking.exception.DuplicateEmailException;
import dev.uts.hanbin.banking.repository.CustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

  private final CustomerRepository customerRepository;

  @Transactional
  public CustomerResponse create(CustomerRequest request) {
    if (customerRepository.existsByEmail(request.email())) {
      throw new DuplicateEmailException(request.email());
    }

    Customer customer = Customer.builder()
        .name(request.name())
        .email(request.email())
        .phone(request.phone())
        .build();

    return CustomerResponse.from(customerRepository.save(customer));
  }

  public List<CustomerResponse> findAll() {
    return customerRepository.findAll().stream()
        .map(CustomerResponse::from)
        .toList();
  }

  public CustomerResponse findById(Long id) {
    return CustomerResponse.from(getCustomer(id));
  }

  @Transactional
  public CustomerResponse update(Long id, CustomerRequest request) {
    Customer customer = getCustomer(id);

    // Only block the email if it already belongs to a different customer.
    boolean emailChanged = !customer.getEmail().equals(request.email());
    if (emailChanged && customerRepository.existsByEmail(request.email())) {
      throw new DuplicateEmailException(request.email());
    }

    customer.update(request.name(), request.email(), request.phone());
    return CustomerResponse.from(customer);
  }

  @Transactional
  public void delete(Long id) {
    customerRepository.delete(getCustomer(id));
  }

  private Customer getCustomer(Long id) {
    return customerRepository.findById(id)
        .orElseThrow(() -> new CustomerNotFoundException(id));
  }
}
