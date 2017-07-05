package com.scanii.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;
import java.util.Map;

/**
 * Generic API response object
 */
@SuppressWarnings("WeakerAccess")
@NotThreadSafe
public class ScaniiResult {
  private String rawResponse;
  private String resourceId;
  private String contentType;
  private long contentLength;
  private String resourceLocation;
  private String requestId;
  private String hostId;
  private List<String> findings = Lists.newArrayList();
  private String checksum;
  private String message;
  private String expirationDate;
  private String creationDate;
  private Map<String, String> metadata = Maps.newHashMap();

  public String getRawResponse() {
    return rawResponse;
  }

  public void setRawResponse(String rawResponse) {
    this.rawResponse = rawResponse;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public long getContentLength() {
    return contentLength;
  }

  public void setContentLength(long contentLength) {
    this.contentLength = contentLength;
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

  public List<String> getFindings() {
    return findings;
  }

  @Override
  public String toString() {
    return "ScaniiResult{" +
      "resourceId='" + resourceId + '\'' +
      ", contentType='" + contentType + '\'' +
      ", contentLength=" + contentLength +
      ", resourceLocation='" + resourceLocation + '\'' +
      ", requestId='" + requestId + '\'' +
      ", hostId='" + hostId + '\'' +
      ", findings=" + findings +
      ", checksum='" + checksum + '\'' +
      ", message='" + message + '\'' +
      ", expirationDate='" + expirationDate + '\'' +
      ", creationDate='" + creationDate + '\'' +
      ", metadata=" + metadata +
      '}';
  }

  public String getChecksum() {
    return checksum;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }
}
