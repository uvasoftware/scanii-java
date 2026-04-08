package com.uvasoftware.scanii;

import com.uvasoftware.scanii.internal.DefaultScaniiClient;
import com.uvasoftware.scanii.models.ScaniiAuthToken;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

class ScaniiClientBuilderTest {

  @Test
  void shouldBuildWithCredentials() {
    ScaniiClient client = ScaniiClients.builder()
      .credentials("key", "secret")
      .build();
    assertNotNull(client);
  }

  @Test
  void shouldBuildWithAuthToken() {
    ScaniiAuthToken token = new ScaniiAuthToken();
    token.setResourceId("token-abc");
    ScaniiClient client = ScaniiClients.builder()
      .authToken(token)
      .build();
    assertNotNull(client);
  }

  @Test
  void shouldDefaultToAutoTarget() {
    ScaniiClient client = ScaniiClients.builder()
      .credentials("key", "secret")
      .build();
    assertNotNull(client);
  }

  @Test
  void shouldAcceptCustomTarget() {
    ScaniiClient client = ScaniiClients.builder()
      .target(ScaniiTarget.EU1)
      .credentials("key", "secret")
      .build();
    assertNotNull(client);
  }

  @Test
  void shouldAcceptCustomHttpClient() {
    HttpClient hc = HttpClient.newHttpClient();
    ScaniiClient client = ScaniiClients.builder()
      .credentials("key", "secret")
      .httpClient(hc)
      .build();
    assertEquals(hc, ((DefaultScaniiClient) client).getHttpClient());
  }

  @Test
  void shouldCreateDefaultHttpClientWhenNotSet() {
    ScaniiClient client = ScaniiClients.builder()
      .credentials("key", "secret")
      .build();
    assertNotNull(((DefaultScaniiClient) client).getHttpClient());
  }

  @Test
  void shouldAcceptCustomUserAgent() {
    ScaniiClient client = ScaniiClients.builder()
      .credentials("key", "secret")
      .userAgent("my-app/2.0")
      .build();
    assertNotNull(client);
  }

  @Test
  void shouldFailWithoutCredentials() {
    assertThrows(IllegalStateException.class, () -> ScaniiClients.builder().build());
  }

  @Test
  void shouldFailWithoutCredentialsEvenWithTarget() {
    assertThrows(IllegalStateException.class, () ->
      ScaniiClients.builder().target(ScaniiTarget.US1).build());
  }

  @Test
  void shouldAllowCredentialsAfterAuthToken() {
    ScaniiAuthToken token = new ScaniiAuthToken();
    token.setResourceId("token-1");
    ScaniiClient client = ScaniiClients.builder()
      .authToken(token)
      .credentials("key", "secret")
      .build();
    assertNotNull(client);
  }

  @Test
  void shouldAllowAuthTokenAfterCredentials() {
    ScaniiAuthToken token = new ScaniiAuthToken();
    token.setResourceId("token-1");
    ScaniiClient client = ScaniiClients.builder()
      .credentials("key", "secret")
      .authToken(token)
      .build();
    assertNotNull(client);
  }

  @Test
  void shouldSupportFluentChaining() {
    ScaniiClientBuilder builder = ScaniiClients.builder();
    assertSame(builder, builder.target(ScaniiTarget.AP1));
    assertSame(builder, builder.credentials("k", "s"));
    assertSame(builder, builder.userAgent("app/1"));
    assertSame(builder, builder.httpClient(HttpClient.newHttpClient()));
  }

  @Test
  void shouldSupportFullConfiguration() {
    HttpClient hc = HttpClient.newHttpClient();
    ScaniiClient client = ScaniiClients.builder()
      .target(ScaniiTarget.AP2)
      .credentials("key", "secret")
      .httpClient(hc)
      .userAgent("full-test/1.0")
      .build();
    assertNotNull(client);
    assertEquals(hc, ((DefaultScaniiClient) client).getHttpClient());
  }

  @Test
  void shouldRejectEmptyKey() {
    assertThrows(IllegalArgumentException.class, () ->
      ScaniiClients.builder().credentials("", "secret").build());
  }

  @Test
  void shouldRejectKeyWithColon() {
    assertThrows(IllegalArgumentException.class, () ->
      ScaniiClients.builder().credentials("a:b", "secret").build());
  }

  @Test
  void shouldRejectNullSecret() {
    assertThrows(IllegalArgumentException.class, () ->
      ScaniiClients.builder().credentials("key", null).build());
  }
}
