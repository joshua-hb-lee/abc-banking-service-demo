package dev.uts.hanbin.banking.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "customers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @Column(length = 20)
  private String phone;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true,
      fetch = FetchType.LAZY)
  private CustomerPreference preference;

  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<CustomerActivity> activities = new ArrayList<>();

  @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<Transaction> transactions = new ArrayList<>();

  @Builder
  public Customer(String name, String email, String phone) {
    this.name = name;
    this.email = email;
    this.phone = phone;
  }

  /** Applies a full update coming from PUT /api/customers/{id}. */
  public void update(String name, String email, String phone) {
    this.name = name;
    this.email = email;
    this.phone = phone;
  }

  public void setPreference(CustomerPreference preference) {
    this.preference = preference;
    if (preference != null) {
      preference.assignCustomer(this);
    }
  }

  public void addActivity(CustomerActivity activity) {
    this.activities.add(activity);
    activity.assignCustomer(this);
  }

  public void addTransaction(Transaction transaction) {
    this.transactions.add(transaction);
    transaction.assignCustomer(this);
  }
}
