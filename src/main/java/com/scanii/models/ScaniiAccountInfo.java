package com.scanii.models;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class ScaniiAccountInfo extends ScaniiResult {
  private String name;
  private long balance;
  private long startingBalance;
  private String billingEmail;
  private String subscription;
  private Instant creationDate;
  private Instant modificationDate;
  private Map<String, User> users;
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
    private Instant creationDate;
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
    private boolean active;
    private Instant creationDate;
    private Instant lastSeenDate;
    private Set<String> detectionCategoriesEnabled;
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
