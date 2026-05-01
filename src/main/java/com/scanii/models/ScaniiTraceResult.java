package com.scanii.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Result of a {@link com.scanii.ScaniiClient#retrieveTrace(String)} call,
 * containing an ordered list of processing events for a given processing id.
 *
 * <p><strong>Preview:</strong> the trace endpoint ({@code GET /v2.2/files/{id}/trace})
 * is marked preview in the v2.2 spec — the API surface may shift before it is marked stable.
 *
 * @see <a href="https://scanii.github.io/openapi/v22/">spec</a>
 */
public class ScaniiTraceResult extends ScaniiResult {
  private String resourceId;
  private List<ScaniiTraceEvent> events = new ArrayList<>();

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public List<ScaniiTraceEvent> getEvents() {
    return events;
  }

  public void setEvents(List<ScaniiTraceEvent> events) {
    this.events = events;
  }

  @Override
  public String toString() {
    return "ScaniiTraceResult{" +
      "resourceId='" + resourceId + '\'' +
      ", events=" + events +
      '}';
  }

  /**
   * A single event in a processing trace.
   */
  public static class ScaniiTraceEvent {
    private Instant timestamp;
    private String message;

    public Instant getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
      this.timestamp = timestamp;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    @Override
    public String toString() {
      return "ScaniiTraceEvent{" +
        "timestamp=" + timestamp +
        ", message='" + message + '\'' +
        '}';
    }
  }
}
