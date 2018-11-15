package com.scanii.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class ScaniiAuthToken extends ScaniiResult {
  @JsonProperty("id")
  private String resourceId;

  @JsonProperty("creation_date")
  private Instant creationDate;

  @JsonProperty("expiration_date")
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
