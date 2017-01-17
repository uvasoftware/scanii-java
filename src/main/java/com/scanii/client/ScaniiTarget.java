package com.scanii.client;

/**
 * Scanii Resource targets so you can control which api version and endpoint you would like your client to utilize.
 *
 * @see <a href="http://docs.scanii.com/v2.1/overview.html#endpoints">http://docs.scanii.com/v2.1/overview.html#endpoints</a>
 */
public enum ScaniiTarget {
  v2_0,
  v2_0_US1,
  v2_0_EU1,
  v2_1,
  v2_1_US1,
  v2_1_EU1,
  v2_0_AP1,
  v2_1_AP1;

  public static ScaniiTarget latest() {
    return ScaniiTarget.v2_1;
  }
}
