package dev.uts.hanbin.banking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_preferences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerPreference {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // One preference row per customer; this side owns the customer_id FK column.
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @Column(name = "notification_enabled")
  private Boolean notificationEnabled = true;

  @Column(name = "preferred_language", length = 10)
  private String preferredLanguage = "en";

  @Column(length = 20)
  private String theme = "light";

  @Builder
  public CustomerPreference(Boolean notificationEnabled, String preferredLanguage, String theme) {
    this.notificationEnabled = notificationEnabled != null ? notificationEnabled : true;
    this.preferredLanguage = preferredLanguage != null ? preferredLanguage : "en";
    this.theme = theme != null ? theme : "light";
  }

  void assignCustomer(Customer customer) {
    this.customer = customer;
  }
}
