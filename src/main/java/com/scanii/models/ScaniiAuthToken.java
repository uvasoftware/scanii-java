package com.scanii.models;

import java.time.Instant;

public class ScaniiAuthToken extends ScaniiResult {
  private String resourceId;
  private Instant creationDate;
  private Instant expirationDate;

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public Instant getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Instant creationDate) {
    this.creationDate = creationDate;
  }

  public Instant getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(Instant expirationDate) {
    this.expirationDate = expirationDate;
  }

  @Override
  public String toString() {
    return "ScaniiAuthToken{" +
      "resourceId='" + resourceId + '\'' +
      ", creationDate=" + creationDate +
      ", expirationDate=" + expirationDate +
      '}';
  }
}
