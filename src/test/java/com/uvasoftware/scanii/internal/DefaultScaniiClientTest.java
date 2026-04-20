package com.uvasoftware.scanii.internal;

import com.uvasoftware.scanii.ScaniiTarget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

class DefaultScaniiClientTest {
  @Test
  void shouldValidateCredentials() {
    // issue 65
    Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultScaniiClient(ScaniiTarget.AP1, "", "secret", HttpClient.newHttpClient(), null, java.util.Collections.emptyMap()));

    Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultScaniiClient(ScaniiTarget.AP1, null, "secret", HttpClient.newHttpClient(), null, java.util.Collections.emptyMap()));

    Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultScaniiClient(ScaniiTarget.AP1, "a:b", "secret", HttpClient.newHttpClient(), null, java.util.Collections.emptyMap()));
  }
}
