package com.uvasoftware.scanii.internal;

import com.uvasoftware.scanii.ScaniiTarget;
import org.apache.hc.client5.http.impl.classic.HttpClients;
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
