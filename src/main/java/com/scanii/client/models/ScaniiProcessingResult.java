package com.scanii.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ScaniiProcessingResult extends ScaniiResult {
  @JsonProperty("id")
  private String resourceId;

  @JsonProperty("content_type")
  private String contentType;

  @JsonProperty("content_length")
  private long contentLength;

  @JsonProperty("findings")
  private List<String> findings = Lists.newArrayList();

  @JsonProperty("checksum")
  private String checksum;

  @JsonProperty("message")
  private String message;

  @JsonProperty("creation_date")
  private Instant creationDate;

  @JsonProperty("metadata")
  private Map<String, String> metadata = Maps.newHashMap();

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

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
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
      ", findings=" + findings +
      ", message='" + message + '\'' +
      ", metadata=" + metadata +
      '}';
  }
}
