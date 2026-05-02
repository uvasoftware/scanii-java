package com.scanii;

import com.scanii.misc.EICAR;
import com.scanii.misc.Systems;
import com.scanii.models.ScaniiAuthToken;
import com.scanii.models.ScaniiPendingResult;
import com.scanii.models.ScaniiProcessingResult;
import com.scanii.models.ScaniiTraceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests against a locally-running scanii-cli mock server.
 *
 * Start the server before running:
 *   docker run -d --name scanii-cli -p 4000:4000 ghcr.io/scanii/scanii-cli:latest server
 *
 * Endpoint: http://localhost:4000  Key: key  Secret: secret
 */
class ScaniiClientTest extends IntegrationTest {
  private static final String ENDPOINT = System.getenv().getOrDefault("SCANII_ENDPOINT", "http://localhost:4000");
  private static final String KEY = "key";
  private static final String SECRET = "secret";

  private ScaniiClient client;

  @BeforeEach
  void before() {
    client = ScaniiClients.createDefault(new ScaniiTarget(ENDPOINT), KEY, SECRET);
  }

  @Test
  void testProcess() throws Exception {
    // clean file — no findings
    ScaniiProcessingResult result = client.process(Systems.randomFile(1024));
    assertNotNull(result.getResourceId());
    assertNotNull(result.getChecksum());
    assertNotNull(result.getResourceLocation());
    assertNotNull(result.getRawResponse());
    assertNotNull(result.getRequestId());
    assertNotNull(result.getContentType());
    assertNotNull(result.getHostId());
    assertNotNull(result.getFindings());
    assertTrue(result.getFindings().isEmpty());

    // EICAR — should trigger finding (decoded in-memory, never written to disk)
    result = client.process(new ByteArrayInputStream(EICAR.decode()));
    assertNotNull(result.getResourceId());
    assertNotNull(result.getFindings());
    assertEquals(1, result.getFindings().size());
    assertEquals("content.malicious.eicar-test-signature", result.getFindings().get(0));
  }

  @Test
  void testProcessWithMetadata() throws Exception {
    ScaniiProcessingResult result = client.process(Systems.randomFile(1024), new HashMap<>() {{
      put("foo", "bar");
    }});
    assertNotNull(result.getResourceId());
    assertEquals("bar", result.getMetadata().get("foo"));
  }

  @Test
  void testProcessInputStreamWithMetadata() throws Exception {
    FileInputStream is = new FileInputStream(Systems.randomFile(1024).toFile());
    ScaniiProcessingResult result = client.process(is, new HashMap<>() {{
      put("foo", "bar");
    }});
    assertNotNull(result.getResourceId());
    assertEquals("bar", result.getMetadata().get("foo"));
  }

  @Test
  void shouldThrowErrorsIfInvalidPost() throws Exception {
    assertThrows(ScaniiException.class, () -> client.process(Files.createTempFile(null, null)));
  }

  @Test
  void shouldThrowErrorsIfInvalidCredentials() {
    ScaniiClient badClient = ScaniiClients.createDefault(new ScaniiTarget(ENDPOINT), "bad-key", "bad-secret");
    assertThrows(ScaniiException.class, () -> badClient.process(Systems.randomFile(1024)));
  }

  @Test
  void testProcessAsync() throws Exception {
    ScaniiPendingResult result = client.processAsync(Systems.randomFile(1024));
    assertNotNull(result.getResourceId());
    assertNotNull(result.getResourceLocation());
    assertNotNull(result.getRawResponse());
    assertNotNull(result.getRequestId());
    assertNotNull(result.getHostId());

    ScaniiProcessingResult actualResult = pollForResult(
      () -> client.retrieve(result.getResourceId()).orElse(null),
      Duration.ofMinutes(1));
    assertNotNull(actualResult.getResourceId());
    assertNotNull(actualResult.getChecksum());
    assertNull(actualResult.getResourceLocation());
    assertNotNull(actualResult.getRawResponse());
    assertNotNull(actualResult.getRequestId());
    assertNotNull(actualResult.getContentType());
    assertNotNull(actualResult.getHostId());
    assertNotNull(actualResult.getFindings());
    assertTrue(actualResult.getFindings().isEmpty());
  }

  @Test
  void testProcessAsyncWithMetadata() throws Exception {
    ScaniiPendingResult result = client.processAsync(Systems.randomFile(1024), new HashMap<>() {{
      put("foo", "bar");
    }});
    ScaniiProcessingResult actualResult = pollForResult(
      () -> client.retrieve(result.getResourceId()).orElse(null),
      Duration.ofMinutes(1));
    assertNotNull(actualResult.getResourceId());
    assertEquals("bar", actualResult.getMetadata().get("foo"));
  }

  @Test
  void testProcessAsyncInputStreamWithMetadata() throws Exception {
    FileInputStream is = new FileInputStream(Systems.randomFile(1024).toFile());
    ScaniiPendingResult result = client.processAsync(is, new HashMap<>() {{
      put("foo", "bar");
    }});
    ScaniiProcessingResult actualResult = pollForResult(
      () -> client.retrieve(result.getResourceId()).orElse(null),
      Duration.ofMinutes(1));
    assertNotNull(actualResult.getResourceId());
    assertEquals("bar", actualResult.getMetadata().get("foo"));
  }

  @Test
  void testFetchWithoutCallback() throws InterruptedException {
    // scanii-cli fetch endpoint: accepts any location URL
    ScaniiPendingResult result = client.fetch("https://scanii.s3.amazonaws.com/eicarcom2.zip");
    assertNotNull(result.getResourceId());
    assertNotNull(result.getResourceLocation());
    assertNotNull(result.getRawResponse());
    assertNotNull(result.getRequestId());
    assertNotNull(result.getHostId());

    ScaniiProcessingResult actualResult = pollForResult(
      () -> client.retrieve(result.getResourceId()).orElse(null),
      Duration.ofMinutes(1));
    assertNotNull(actualResult.getResourceId());
  }

  @Test
  void testPing() {
    assertTrue(client.ping());
  }

  @Test
  void testCreateAuthToken() {
    ScaniiAuthToken result = client.createAuthToken(1, TimeUnit.HOURS);
    assertNotNull(result.getResourceId());
  }

  @Test
  void testDeleteAuthToken() {
    ScaniiAuthToken result = client.createAuthToken(1, TimeUnit.HOURS);
    assertTrue(client.deleteAuthToken(result.getResourceId()));
  }

  @Test
  void testRetrieveAuthToken() {
    ScaniiAuthToken result = client.createAuthToken(1, TimeUnit.HOURS);
    ScaniiAuthToken result2 = client.retrieveAuthToken(result.getResourceId());
    assertEquals(result.getResourceId(), result2.getResourceId());
  }

  @Test
  void testRetrieveTrace() throws Exception {
    ScaniiProcessingResult processed = client.process(Systems.randomFile(1024));
    assertNotNull(processed.getResourceId());

    Optional<ScaniiTraceResult> opt = client.retrieveTrace(processed.getResourceId());
    assertTrue(opt.isPresent());
    ScaniiTraceResult trace = opt.get();
    assertEquals(processed.getResourceId(), trace.getResourceId());
    assertNotNull(trace.getEvents());
    assertFalse(trace.getEvents().isEmpty());
    for (ScaniiTraceResult.ScaniiTraceEvent event : trace.getEvents()) {
      assertNotNull(event.getTimestamp());
      assertNotNull(event.getMessage());
    }
  }

  @Test
  void testRetrieveTraceUnknownId() {
    Optional<ScaniiTraceResult> opt = client.retrieveTrace("doesnotexist");
    assertTrue(opt.isEmpty());
  }

  @Test
  void testProcessFromUrl() throws Exception {
    // scanii-cli serves the EICAR file at this well-known path
    ScaniiProcessingResult result = client.processFromUrl(URI.create(ENDPOINT + "/static/eicar.txt"));
    assertNotNull(result.getResourceId());
    assertNotNull(result.getChecksum());
    assertNotNull(result.getRequestId());
    assertNotNull(result.getHostId());
    assertNotNull(result.getFindings());
  }

  /**
   * TODO: callback integration test — requires scanii-cli callback support.
   * See RAFAEL_CHECKLIST.md §1.6 for the prerequisite work. Once scanii-cli
   * ships callback simulation, implement this test by spinning up a local HTTP
   * server, passing its URL as the callback parameter, and asserting the payload.
   */
  @Test
  @Disabled("TODO: scanii-cli callback support not yet available — see RAFAEL_CHECKLIST.md §1.6")
  void testProcessWithCallback() {
    // stub — implement after scanii-cli adds callback simulation
  }
}
