package com.uvasoftware.scanii;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Scanii Resource targets so you can control which api version and endpoint you would like your client to utilize.
 *
 * @see <a href="http://docs.scanii.com/v2.1/overview.html#endpoints">http://docs.scanii.com/v2.1/overview.html#endpoints</a>
 */
public class ScaniiTarget {
  public static final ScaniiTarget AUTO = new ScaniiTarget("https://api.scanii.com");
  public static final ScaniiTarget US1 = new ScaniiTarget("https://api-us1.scanii.com");
  public static final ScaniiTarget EU1 = new ScaniiTarget("https://api-eu1.scanii.com");
  public static final ScaniiTarget EU2 = new ScaniiTarget("https://api-eu2.scanii.com");
  public static final ScaniiTarget AP1 = new ScaniiTarget("https://api-ap1.scanii.com");
  public static final ScaniiTarget AP2 = new ScaniiTarget("https://api-ap2.scanii.com");

  private final URI endpoint;

  public ScaniiTarget(String url) {
    this.endpoint = URI.create(url);
  }

  protected static List<ScaniiTarget> all() {
    return Stream.of(AUTO, US1, EU1, EU2, AP1, AP2).collect(Collectors.toList());
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
