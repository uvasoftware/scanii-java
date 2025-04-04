package com.uvasoftware.scanii;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

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

  @Test
  void shouldReturnAll() {
    var allEndpoints = ScaniiTarget.all();
    Assertions.assertEquals(7, allEndpoints.size());
    for (ScaniiTarget target : List.of(ScaniiTarget.AUTO, ScaniiTarget.US1, ScaniiTarget.EU1, ScaniiTarget.EU2, ScaniiTarget.AP1, ScaniiTarget.AP2, ScaniiTarget.CA1)) {
      Assertions.assertTrue(allEndpoints.contains(target), "All did not include region " + target);
    }
  }

  @Test
  void getEndpoint() {
    for (ScaniiTarget target : ScaniiTarget.all()) {
      Assertions.assertNotNull(target.getEndpoint());
    }
  }
}
