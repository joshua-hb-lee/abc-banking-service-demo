package dev.uts.hanbin.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.uts.hanbin.banking.dto.CustomerRequest;
import dev.uts.hanbin.banking.dto.CustomerResponse;
import dev.uts.hanbin.banking.entity.Customer;
import dev.uts.hanbin.banking.entity.CustomerActivity;
import dev.uts.hanbin.banking.entity.CustomerPreference;
import dev.uts.hanbin.banking.exception.CustomerNotFoundException;
import dev.uts.hanbin.banking.exception.DuplicateEmailException;
import dev.uts.hanbin.banking.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CustomerServiceTest {

  @Autowired
  private CustomerService customerService;

  @Autowired
  private CustomerRepository customerRepository;

  private static CustomerRequest request(String email) {
    return new CustomerRequest("Hanbin Lee", email, "0400000000");
  }

  @Test
  void createsAndReadsBackCustomer() {
    CustomerResponse created = customerService.create(request("create@abc.com"));

    assertThat(created.id()).isNotNull();
    assertThat(created.createdAt()).isNotNull();
    assertThat(customerService.findById(created.id()).email()).isEqualTo("create@abc.com");
  }

  @Test
  void rejectsDuplicateEmailOnCreate() {
    customerService.create(request("dup@abc.com"));

    assertThatThrownBy(() -> customerService.create(request("dup@abc.com")))
        .isInstanceOf(DuplicateEmailException.class);
  }

  @Test
  void updatesCustomerAndAllowsKeepingOwnEmail() {
    Long id = customerService.create(request("update@abc.com")).id();

    CustomerResponse updated =
        customerService.update(id, new CustomerRequest("Updated", "update@abc.com", "0411111111"));

    assertThat(updated.name()).isEqualTo("Updated");
    assertThat(updated.phone()).isEqualTo("0411111111");
  }

  @Test
  void deleteCascadesToPreferenceAndActivities() {
    Customer customer = Customer.builder()
        .name("Hanbin Lee")
        .email("cascade@abc.com")
        .phone("0400000000")
        .build();
    customer.setPreference(CustomerPreference.builder().theme("dark").build());
    customer.addActivity(CustomerActivity.builder()
        .activityType("LOGIN")
        .description("Signed in from the mobile app")
        .build());
    Long id = customerRepository.saveAndFlush(customer).getId();

    customerService.delete(id);

    assertThat(customerRepository.findById(id)).isEmpty();
  }

  @Test
  void throwsWhenCustomerMissing() {
    assertThatThrownBy(() -> customerService.findById(-1L))
        .isInstanceOf(CustomerNotFoundException.class);
  }
}
