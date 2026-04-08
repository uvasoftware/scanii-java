package com.uvasoftware.scanii.models;

public class ScaniiPendingResult extends ScaniiResult {
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
