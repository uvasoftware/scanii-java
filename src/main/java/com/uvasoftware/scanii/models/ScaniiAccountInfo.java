package com.uvasoftware.scanii.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class ScaniiAccountInfo extends ScaniiResult {
  @JsonProperty("name")
  private String name;
  @JsonProperty("balance")
  private long balance;
  @JsonProperty("starting_balance")
  private long startingBalance;
  @JsonProperty("billing_email")
  private String billingEmail;
  @JsonProperty("subscription")
  private String subscription;
  @JsonProperty("creation_date")
  private Instant creationDate;
  @JsonProperty("modification_date")
  private Instant modificationDate;
  @JsonProperty("users")
  private Map<String, User> users;
  @JsonProperty("keys")
  private Map<String, ApiKey> keys;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getBalance() {
    return balance;
  }

  public void setBalance(long balance) {
    this.balance = balance;
  }

  public long getStartingBalance() {
    return startingBalance;
  }

  public void setStartingBalance(long startingBalance) {
    this.startingBalance = startingBalance;
  }

  public String getBillingEmail() {
    return billingEmail;
  }

  public void setBillingEmail(String billingEmail) {
    this.billingEmail = billingEmail;
  }

  public String getSubscription() {
    return subscription;
  }

  public void setSubscription(String subscription) {
    this.subscription = subscription;
  }

  public Instant getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Instant creationDate) {
    this.creationDate = creationDate;
  }

  public Instant getModificationDate() {
    return modificationDate;
  }

  public void setModificationDate(Instant modificationDate) {
    this.modificationDate = modificationDate;
  }

  public Map<String, User> getUsers() {
    return users;
  }

  public void setUsers(Map<String, User> users) {
    this.users = users;
  }

  public Map<String, ApiKey> getKeys() {
    return keys;
  }

  public void setKeys(Map<String, ApiKey> keys) {
    this.keys = keys;
  }

  @Override
  public String toString() {
    return "ScaniiAccountInfo{" +
      "name='" + name + '\'' +
      ", balance=" + balance +
      ", startingBalance=" + startingBalance +
      ", billingEmail='" + billingEmail + '\'' +
      ", subscription='" + subscription + '\'' +
      ", creationDate=" + creationDate +
      ", modificationDate=" + modificationDate +
      ", users=" + users +
      ", keys=" + keys +
      '}';
  }

  public static class User {
    @JsonProperty("creation_date")
    private Instant creationDate;
    @JsonProperty("last_login_date")
    private Instant lastLoginDate;

    public Instant getCreationDate() {
      return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
      this.creationDate = creationDate;
    }

    public Instant getLastLoginDate() {
      return lastLoginDate;
    }

    public void setLastLoginDate(Instant lastLoginDate) {
      this.lastLoginDate = lastLoginDate;
    }
  }

  public static class ApiKey {
    @JsonProperty("active")
    private boolean active;
    @JsonProperty("creation_date")
    private Instant creationDate;
    @JsonProperty("last_seen_date")
    private Instant lastSeenDate;
    @JsonProperty("detection_categories_enabled")
    private Set<String> detectionCategoriesEnabled;
    @JsonProperty("tags")
    private Set<String> tags;

    public boolean isActive() {
      return active;
    }

    public void setActive(boolean active) {
      this.active = active;
    }

    public Instant getCreationDate() {
      return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
      this.creationDate = creationDate;
    }

    public Instant getLastSeenDate() {
      return lastSeenDate;
    }

    public void setLastSeenDate(Instant lastSeenDate) {
      this.lastSeenDate = lastSeenDate;
    }

    public Set<String> getDetectionCategoriesEnabled() {
      return detectionCategoriesEnabled;
    }

    public void setDetectionCategoriesEnabled(Set<String> detectionCategoriesEnabled) {
      this.detectionCategoriesEnabled = detectionCategoriesEnabled;
    }

    public Set<String> getTags() {
      return tags;
    }

    public void setTags(Set<String> tags) {
      this.tags = tags;
    }
  }
}
