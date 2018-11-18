package com.uvasoftware.scanii.models;

public class ScaniiResult {
  private String rawResponse;
  private String resourceLocation;
  private String requestId;
  private String hostId;
  private int statusCode;

  public String getRawResponse() {
    return rawResponse;
  }

  public void setRawResponse(String rawResponse) {
    this.rawResponse = rawResponse;
  }

  public String getResourceLocation() {
    return resourceLocation;
  }

  public void setResourceLocation(String resourceLocation) {
    this.resourceLocation = resourceLocation;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getHostId() {
    return hostId;
  }

  public void setHostId(String hostId) {
    this.hostId = hostId;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }
}
