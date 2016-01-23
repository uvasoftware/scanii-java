package com.scanii.client.misc;

import com.scanii.client.ScaniiTarget;

import java.util.HashMap;
import java.util.Map;

public class Endpoints {
  private static final Map<ScaniiTarget, String> mapping = new HashMap<ScaniiTarget, String>();

  static {
    mapping.put(ScaniiTarget.v2_0, "https://api.scanii.com/v2.0");
    mapping.put(ScaniiTarget.v2_0_EU1, "https://api-eu1.scanii.com/v2.0");
    mapping.put(ScaniiTarget.v2_0_US1, "https://api-us1.scanii.com/v2.0");
    mapping.put(ScaniiTarget.v2_1_US1, "https://api-us1.scanii.com/v2.1");
    mapping.put(ScaniiTarget.v2_1_EU1, "https://api-us1.scanii.com/v2.1");
    mapping.put(ScaniiTarget.v2_1, "https://api.scanii.com/v2.1");
    mapping.put(ScaniiTarget.LOCAL, "http://localhost:8081/v2.1");
  }

  public static String resolve(ScaniiTarget version) {
    return mapping.get(version);
  }

  public static String resolve(ScaniiTarget target, String path) {
    return String.format("%s/%s", resolve(target), path);
  }
}
