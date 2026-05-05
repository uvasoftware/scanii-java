package com.scanii;

import java.net.URI;
import java.util.List;

/**
 * Scanii regional API endpoints.
 *
 * @see <a href="https://scanii.github.io/openapi/v22/">https://scanii.github.io/openapi/v22/</a>
 */
public class ScaniiTarget {
  /**
   * Latency-routed endpoint ({@code https://api.scanii.com}). Routes to the nearest regional
   * endpoint automatically, but does not guarantee which region processes your data.
   *
   * @deprecated Use an explicit regional target for data residency compliance:
   *   {@link #US1}, {@link #EU1}, {@link #EU2}, {@link #AP1}, {@link #AP2}, {@link #CA1}.
   *   Will be removed in a future major version.
   */
  @Deprecated(since = "8.2.0")
  public static final ScaniiTarget AUTO = new ScaniiTarget("https://api.scanii.com");
  public static final ScaniiTarget US1 = new ScaniiTarget("https://api-us1.scanii.com");
  public static final ScaniiTarget EU1 = new ScaniiTarget("https://api-eu1.scanii.com");
  public static final ScaniiTarget EU2 = new ScaniiTarget("https://api-eu2.scanii.com");
  public static final ScaniiTarget AP1 = new ScaniiTarget("https://api-ap1.scanii.com");
  public static final ScaniiTarget AP2 = new ScaniiTarget("https://api-ap2.scanii.com");
  public static final ScaniiTarget CA1 = new ScaniiTarget("https://api-ca1.scanii.com");

  private final URI endpoint;

  public ScaniiTarget(String url) {
    this.endpoint = URI.create(url);
  }

  protected static List<ScaniiTarget> all() {
    return List.of(AUTO, US1, EU1, EU2, AP1, AP2, CA1);
  }

  public String resolve(String path) {
    return endpoint.resolve(path).toString();
  }

  public URI getEndpoint() {
    return endpoint;
  }

  @Override
  public String toString() {
    return "ScaniiTarget{" +
      "endpoint=" + endpoint +
      '}';
  }
}
