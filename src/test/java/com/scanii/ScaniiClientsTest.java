package com.scanii;

import com.scanii.internal.DefaultScaniiClient;
import com.scanii.models.ScaniiAuthToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

@SuppressWarnings("deprecation")
class ScaniiClientsTest {

  @Test
  void createDefault() {
    ScaniiClient client = ScaniiClients.createDefault(ScaniiTarget.AUTO, "key", "secret");
    Assertions.assertNotNull(client);
  }

  @Test
  void createDefault1() {
    ScaniiAuthToken token = new ScaniiAuthToken();
    token.setResourceId("123");
    ScaniiClient client = ScaniiClients.createDefault(ScaniiTarget.AUTO, token);
    Assertions.assertNotNull(client);
  }

  @Test
  void createDefault2() {
    HttpClient hc = HttpClient.newHttpClient();
    ScaniiClient client = ScaniiClients.createDefault(ScaniiTarget.AUTO, "key", "secret", hc);
    Assertions.assertNotNull(client);
    Assertions.assertEquals(hc, ((DefaultScaniiClient) client).getHttpClient());
  }

  @Test
  void builderWithCredentials() {
    ScaniiClient client = ScaniiClients.builder()
      .target(ScaniiTarget.EU1)
      .credentials("key", "secret")
      .build();
    Assertions.assertNotNull(client);
  }

  @Test
  void builderWithAuthToken() {
    ScaniiAuthToken token = new ScaniiAuthToken();
    token.setResourceId("token-123");
    ScaniiClient client = ScaniiClients.builder()
      .target(ScaniiTarget.US1)
      .authToken(token)
      .build();
    Assertions.assertNotNull(client);
  }

  @Test
  void builderWithCustomUserAgent() {
    ScaniiClient client = ScaniiClients.builder()
      .credentials("key", "secret")
      .userAgent("my-app/2.0")
      .build();
    Assertions.assertNotNull(client);
  }

  @Test
  void builderWithCustomHttpClient() {
    HttpClient hc = HttpClient.newHttpClient();
    ScaniiClient client = ScaniiClients.builder()
      .credentials("key", "secret")
      .httpClient(hc)
      .build();
    Assertions.assertNotNull(client);
    Assertions.assertEquals(hc, ((DefaultScaniiClient) client).getHttpClient());
  }

  @Test
  void builderDefaultsToAutoTarget() {
    ScaniiClient client = ScaniiClients.builder()
      .credentials("key", "secret")
      .build();
    Assertions.assertNotNull(client);
  }

  @Test
  void builderShouldFailWithoutCredentials() {
    Assertions.assertThrows(IllegalStateException.class, () -> ScaniiClients.builder().build());
  }
}
