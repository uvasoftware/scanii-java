package com.uvasoftware.scanii;

import com.google.common.collect.ImmutableMap;
import com.uvasoftware.scanii.misc.EICAR;
import com.uvasoftware.scanii.misc.Systems;
import com.uvasoftware.scanii.misc.Threads;
import com.uvasoftware.scanii.models.ScaniiAccountInfo;
import com.uvasoftware.scanii.models.ScaniiAuthToken;
import com.uvasoftware.scanii.models.ScaniiPendingResult;
import com.uvasoftware.scanii.models.ScaniiProcessingResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

class ScaniiClientTest {
  private static final String KEY;
  private static final String SECRET;

  static {
    KEY = System.getenv("SCANII_CREDS").split(":")[0];
    SECRET = System.getenv("SCANII_CREDS").split(":")[1];
  }

  private final Path eicarFile;
  private ScaniiClient client;

  ScaniiClientTest() throws IOException {
    this.eicarFile = Files.write(Files.createTempFile(null, null), EICAR.SIGNATURE.getBytes());
  }

  @BeforeEach
  void before() {
    client = ScaniiClients.createDefault(ScaniiTarget.latest(), KEY, SECRET);
  }

  @ParameterizedTest
  @EnumSource(value = ScaniiTarget.class, names = {"v2_0", "v2_1"})
  void testProcess() throws Exception {

    ScaniiProcessingResult result;
    // simple processing clean
    result = client.process(Systems.randomFile(1024));
    Assertions.assertNotNull(result.getResourceId());
    Assertions.assertNotNull(result.getChecksum());
    Assertions.assertNotNull(result.getResourceLocation());
    Assertions.assertNotNull(result.getRawResponse());
    Assertions.assertNotNull(result.getRequestId());
    Assertions.assertNotNull(result.getContentType());
    Assertions.assertNotNull(result.getHostId());
    Assertions.assertNotNull(result.getFindings());
    Assertions.assertTrue(result.getFindings().isEmpty());
    System.out.println(result);

    // with findings
    result = client.process(eicarFile);
    Assertions.assertNotNull(result.getResourceId());
    Assertions.assertNotNull(result.getChecksum());
    Assertions.assertNotNull(result.getResourceLocation());
    Assertions.assertNotNull(result.getRawResponse());
    Assertions.assertNotNull(result.getRequestId());
    Assertions.assertNotNull(result.getContentType());
    Assertions.assertNotNull(result.getHostId());
    Assertions.assertNotNull(result.getFindings());
    Assertions.assertEquals(1, result.getFindings().size());
    Assertions.assertEquals("content.malicious.eicar-test-signature", result.getFindings().get(0));
    System.out.println(result);

    // failures:

  }

  @Test
  void testProcessWithMetadata() throws Exception {

    ScaniiProcessingResult result;

    // simple processing clean
    result = client.process(Systems.randomFile(1024), ImmutableMap.of("foo", "bar"));
    Assertions.assertNotNull(result.getResourceId());
    Assertions.assertEquals("bar", result.getMetadata().get("foo"));
    System.out.println(result);
  }

  @Test
  void testProcessInputStreamWithMetadata() throws Exception {

    ScaniiProcessingResult result;

    FileInputStream is = new FileInputStream(Systems.randomFile(1024).toFile());

    // simple processing clean
    result = client.process(is, ImmutableMap.of("foo", "bar"));
    Assertions.assertNotNull(result.getResourceId());
    Assertions.assertEquals("bar", result.getMetadata().get("foo"));
    System.out.println(result);
  }

  @Test
  void testProcessWithMetadataAndCallback() throws Exception {
    ScaniiProcessingResult result;

    // simple processing clean
    result = client.process(Systems.randomFile(1024), "https://httpbin.org/post", ImmutableMap.of("foo", "bar"));
    Assertions.assertNotNull(result.getResourceId());
    Assertions.assertEquals("bar", result.getMetadata().get("foo"));
    System.out.println(result);
  }


  @Test
  void testProcessInputStreamWithMetadataAndCallback() throws Exception {
    ScaniiProcessingResult result;

    FileInputStream is = new FileInputStream(Systems.randomFile(1024).toFile());

    // simple processing clean
    result = client.process(is, "https://httpbin.org/post", ImmutableMap.of("foo", "bar"));
    Assertions.assertNotNull(result.getResourceId());
    Assertions.assertEquals("bar", result.getMetadata().get("foo"));
    System.out.println(result);
  }


  @ParameterizedTest
  @EnumSource(value = ScaniiTarget.class, names = {"v2_0", "v2_1"})
  void shouldThrowErrorsIfInvalidPost() {
    // empty file:
    Assertions.assertThrows(ScaniiException.class, () -> {
      client.process(Files.createTempFile(null, null));
    });
  }

  @ParameterizedTest
  @EnumSource(value = ScaniiTarget.class, names = {"v2_0", "v2_1"})
  void shouldThrowErrorsIfInvalidCredentials() {
    ScaniiClient client = ScaniiClients.createDefault(ScaniiTarget.v2_0, "foo", "bar");

    // empty file:
    Assertions.assertThrows(ScaniiException.class, () -> {
      client.process(Files.createTempFile(null, null));
    });

  }

  @ParameterizedTest
  @EnumSource(value = ScaniiTarget.class, names = {"v2_0", "v2_1"})
  void testProcessAsync() throws Exception {

    ScaniiPendingResult result = client.processAsync(Systems.randomFile(1024));
    Assertions.assertNotNull(result.getResourceId());
    Assertions.assertNotNull(result.getResourceLocation());
    Assertions.assertNotNull(result.getRawResponse());
    Assertions.assertNotNull(result.getRequestId());
    Assertions.assertNotNull(result.getHostId());
    System.out.println(result);

    Thread.sleep(1000);

    // now fetching the retrieve
    ScaniiProcessingResult actualResult = client.retrieve(result.getResourceId()).orElseThrow(IllegalStateException::new);
    Assertions.assertNotNull(actualResult.getResourceId());
    Assertions.assertNotNull(actualResult.getChecksum());
    Assertions.assertNull(actualResult.getResourceLocation());
    Assertions.assertNotNull(actualResult.getRawResponse());
    Assertions.assertNotNull(actualResult.getRequestId());
    Assertions.assertNotNull(actualResult.getContentType());
    Assertions.assertNotNull(actualResult.getHostId());
    Assertions.assertNotNull(actualResult.getFindings());
    Assertions.assertTrue(actualResult.getFindings().isEmpty());

  }

  @Test
  void testProcessAsyncWithMetadata() throws Exception {
    ScaniiPendingResult result = client.processAsync(Systems.randomFile(1024), ImmutableMap.of("foo", "bar"));
    Thread.sleep(1000);

    // now fetching the retrieve
    ScaniiProcessingResult actualResult = client.retrieve(result.getResourceId()).orElseThrow(IllegalStateException::new);
    Assertions.assertNotNull(actualResult.getResourceId());
    Assertions.assertEquals("bar", actualResult.getMetadata().get("foo"));
  }

  @Test
  void testProcessAsyncInputStreamWithMetadata() throws Exception {

    FileInputStream is = new FileInputStream(Systems.randomFile(1024).toFile());

    ScaniiPendingResult result = client.processAsync(is, ImmutableMap.of("foo", "bar"));
    Thread.sleep(1000);

    // now fetching the retrieve
    ScaniiProcessingResult actualResult = client.retrieve(result.getResourceId()).orElseThrow(IllegalStateException::new);
    Assertions.assertNotNull(actualResult.getResourceId());
    Assertions.assertEquals("bar", actualResult.getMetadata().get("foo"));
  }

  @Test
  void testProcessAsyncWithMetadataAndCallback() throws Exception {
    ScaniiPendingResult result = client.processAsync(Systems.randomFile(1024), "https://httpbin.org/post", ImmutableMap.of("foo", "bar"));
    Thread.sleep(1000);

    // now fetching the retrieve
    ScaniiProcessingResult actualResult = client.retrieve(result.getResourceId()).orElseThrow(IllegalStateException::new);
    Assertions.assertNotNull(actualResult.getResourceId());
    Assertions.assertEquals("bar", actualResult.getMetadata().get("foo"));
  }

  @Test
  void testProcessAsyncInputStreamWithMetadataAndCallback() throws Exception {

    FileInputStream is = new FileInputStream(Systems.randomFile(1024).toFile());

    ScaniiPendingResult result = client.processAsync(is, "https://httpbin.org/post", ImmutableMap.of("foo", "bar"));
    Thread.sleep(1000);

    // now fetching the retrieve
    ScaniiProcessingResult actualResult = client.retrieve(result.getResourceId()).orElseThrow(IllegalStateException::new);
    Assertions.assertNotNull(actualResult.getResourceId());
    Assertions.assertEquals("bar", actualResult.getMetadata().get("foo"));
  }

  @ParameterizedTest
  @EnumSource(value = ScaniiTarget.class, names = {"v2_0", "v2_1"})
  void testFetchWithoutCallback() throws InterruptedException {

    // simple processing clean
    ScaniiPendingResult result = client.fetch("https://scanii.s3.amazonaws.com/eicarcom2.zip");
    Assertions.assertNotNull(result.getResourceId());
    Assertions.assertNotNull(result.getResourceLocation());
    Assertions.assertNotNull(result.getRawResponse());
    Assertions.assertNotNull(result.getRequestId());
    Assertions.assertNotNull(result.getHostId());
    System.out.println(result);

    Thread.sleep(1000);

    // fetching result:
    ScaniiProcessingResult actualResult = client.retrieve(result.getResourceId()).orElseThrow(IllegalStateException::new);
    Assertions.assertNotNull(actualResult.getResourceId());
    Assertions.assertNotNull(actualResult.getChecksum());
    Assertions.assertNull(actualResult.getResourceLocation());
    Assertions.assertNotNull(actualResult.getRawResponse());
    Assertions.assertNotNull(actualResult.getRequestId());
    Assertions.assertNotNull(actualResult.getContentType());
    Assertions.assertNotNull(actualResult.getHostId());
    Assertions.assertNotNull(actualResult.getFindings());
    Assertions.assertEquals("content.malicious.eicar-test-signature", actualResult.getFindings().get(0));
  }

  @ParameterizedTest
  @EnumSource(value = ScaniiTarget.class, names = {"v2_0", "v2_1"})
  void testFetchWithCallback() throws InterruptedException {

    // simple processing clean
    ScaniiPendingResult result = client.fetch("https://scanii.s3.amazonaws.com/eicarcom2.zip", "https://httpbin.org/post");
    Assertions.assertNotNull(result.getResourceId());
    Assertions.assertNotNull(result.getResourceLocation());
    Assertions.assertNotNull(result.getRawResponse());
    Assertions.assertNotNull(result.getRequestId());
    Assertions.assertNotNull(result.getHostId());
    System.out.println(result);

    AtomicReference<ScaniiProcessingResult> actualResult = new AtomicReference<>();
    Threads.waitUntil(() -> {
      Optional<ScaniiProcessingResult> potentialResult = client.retrieve(result.getResourceId());
      if (potentialResult.isPresent()) {
        actualResult.set(potentialResult.get());
        return true;
      }
      return false;

    }, 5, TimeUnit.SECONDS);

    Assertions.assertNotNull(actualResult.get().getResourceId());
    Assertions.assertNotNull(actualResult.get().getChecksum());
    Assertions.assertNull(actualResult.get().getResourceLocation());
    Assertions.assertNotNull(actualResult.get().getRawResponse());
    Assertions.assertNotNull(actualResult.get().getRequestId());
    Assertions.assertNotNull(actualResult.get().getContentType());
    Assertions.assertNotNull(actualResult.get().getHostId());
    Assertions.assertNotNull(actualResult.get().getFindings());
    Assertions.assertEquals("content.malicious.eicar-test-signature", actualResult.get().getFindings().get(0));
  }

  @Test
  void testFetchWithMetadata() throws Exception {

    ScaniiPendingResult result = client.fetch("https://scanii.s3.amazonaws.com/eicarcom2.zip", "http://google.com", ImmutableMap.of("foo", "bar"));
    Assertions.assertNotNull(result.getResourceId());

    final ScaniiProcessingResult[] actualResult = {null};

    Threads.waitUntil(() -> {
      try {
        System.out.println("attempting to load result " + result.getResourceId());
        Optional<ScaniiProcessingResult> actualResultOptional = client.retrieve(result.getResourceId());
        if (actualResultOptional.isPresent()) {
          actualResult[0] = actualResultOptional.get();
          return true;
        }
      } catch (ScaniiException ignored) {
      }
      return false;
    }, 5, TimeUnit.SECONDS);

    Assertions.assertNotNull(actualResult[0]);
    Assertions.assertEquals("bar", actualResult[0].getMetadata().get("foo"));
    System.out.println(result);


  }

  @ParameterizedTest
  @EnumSource(value = ScaniiTarget.class, names = {"v2_0", "v2_1"})
  void testPing() {
    Assertions.assertTrue(client.ping());
  }

  @ParameterizedTest
  @EnumSource(value = ScaniiTarget.class, names = {"v2_0", "v2_1"})
  void testCreateAuthToken() throws Exception {
    ScaniiAuthToken result = client.createAuthToken(1, TimeUnit.HOURS);
    Assertions.assertNotNull(result.getResourceId());
    Assertions.assertNotNull(result.getExpirationDate());
    Assertions.assertNotNull(result.getCreationDate());

    // now using the auth token to create a new client and process content
    ScaniiClient tempClient = ScaniiClients.createDefault(ScaniiTarget.latest(), result.getResourceId());
    ScaniiProcessingResult processingResult = tempClient.process(Systems.randomFile(1024));
    Assertions.assertNotNull(processingResult.getResourceId());
    Assertions.assertNotNull(processingResult.getChecksum());
    Assertions.assertNotNull(processingResult.getResourceLocation());
    Assertions.assertNotNull(processingResult.getRawResponse());
    Assertions.assertNotNull(processingResult.getRequestId());
    Assertions.assertNotNull(processingResult.getContentType());
    Assertions.assertNotNull(processingResult.getHostId());
    Assertions.assertNotNull(processingResult.getFindings());
    Assertions.assertTrue(processingResult.getFindings().isEmpty());
    System.out.println(processingResult);

  }

  @ParameterizedTest
  @EnumSource(value = ScaniiTarget.class, names = {"v2_0", "v2_1"})
  void testDeleteAuthToken() {
    ScaniiAuthToken result = client.createAuthToken(1, TimeUnit.HOURS);
    client.deleteAuthToken(result.getResourceId());
  }

  @ParameterizedTest
  @EnumSource(value = ScaniiTarget.class, names = {"v2_0", "v2_1"})
  void testRetrieveAuthToken() {
    ScaniiAuthToken result = client.createAuthToken(1, TimeUnit.HOURS);
    ScaniiAuthToken result2 = client.retrieveAuthToken(result.getResourceId());
    Assertions.assertEquals(result.getResourceId(), result2.getResourceId());
    Assertions.assertEquals(result.getCreationDate(), result2.getCreationDate());
    Assertions.assertEquals(result.getExpirationDate(), result2.getExpirationDate());
  }


  @Test
  void shouldPingAllRegions() {
    for (ScaniiTarget target : ScaniiTarget.values()) {
      System.out.println(target);
      ScaniiClient client = ScaniiClients.createDefault(target, KEY, SECRET);
      Assertions.assertTrue(client.ping());
    }
  }

  @Test
  void shouldRetrieveAccountInfo() {
    ScaniiAccountInfo account = client.retrieveAccountInfo();
    Assertions.assertNotNull(account.getName());
    Assertions.assertTrue(account.getBalance() > 0);
    Assertions.assertTrue(account.getStartingBalance() > 0);
    Assertions.assertNotNull(account.getCreationDate());
    Assertions.assertNotNull(account.getModificationDate());
    Assertions.assertTrue(account.getUsers().size() > 0);
    Assertions.assertTrue(account.getKeys().size() > 0);
  }
}
