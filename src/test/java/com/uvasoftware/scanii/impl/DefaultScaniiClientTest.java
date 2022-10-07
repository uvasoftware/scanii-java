package com.uvasoftware.scanii.impl;

import com.uvasoftware.scanii.ScaniiTarget;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultScaniiClientTest {
  @Test
  void shouldValidateCredentials() {
    // issue 65
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new DefaultScaniiClient(ScaniiTarget.AP1, "", "secret", HttpClients.createDefault());
    });

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new DefaultScaniiClient(ScaniiTarget.AP1, null, "secret", HttpClients.createDefault());
    });

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new DefaultScaniiClient(ScaniiTarget.AP1, "a:b", "secret", HttpClients.createDefault());
    });
  }
}
