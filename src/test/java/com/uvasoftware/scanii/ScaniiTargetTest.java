package com.uvasoftware.scanii;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScaniiTargetTest {

  @Test
  void shouldPointToCorrectUrl() {
    Assertions.assertTrue(ScaniiTarget.AUTO.resolve("/").contains("api.scanii.com"));
    Assertions.assertTrue(ScaniiTarget.US1.resolve("/").contains("api-us1.scanii.com"));
  }

  @Test
  void shouldResolvePaths() {
    ScaniiTarget target = new ScaniiTarget("http://example.com");
    Assertions.assertEquals("http://example.com/v2.1/api", target.resolve("/v2.1/api"));
  }
}
