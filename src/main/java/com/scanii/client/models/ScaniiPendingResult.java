package com.scanii.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScaniiPendingResult extends ScaniiResult {
  @JsonProperty("id")
  private String resourceId;

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  @Override
  public String toString() {
    return "ScaniiPendingResult{" +
      "resourceId='" + resourceId + '\'' +
      '}';
  }
}
