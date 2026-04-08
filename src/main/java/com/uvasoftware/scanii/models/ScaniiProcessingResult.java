package com.uvasoftware.scanii.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScaniiProcessingResult extends ScaniiResult {
  private String resourceId;
  private String contentType;
  private long contentLength;
  private List<String> findings = new ArrayList<>();
  private String checksum;
  private Instant creationDate;
  private Map<String, String> metadata = new HashMap<>();

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

  public List<String> getFindings() {
    return findings;
  }

  public void setFindings(List<String> findings) {
    this.findings = findings;
  }

  public String getChecksum() {
    return checksum;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  public Instant getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Instant creationDate) {
    this.creationDate = creationDate;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }

  @Override
  public String toString() {
    return "ScaniiProcessingResult{" +
      "resourceId='" + resourceId + '\'' +
      ", contentType='" + contentType + '\'' +
      ", contentLength=" + contentLength +
      ", findings=" + findings +
      ", checksum='" + checksum + '\'' +
      ", metadata=" + metadata +
      '}';
  }
}
