package com.scanii.client;

import com.google.common.collect.ImmutableMap;
import com.scanii.client.misc.EICAR;
import com.scanii.client.misc.Systems;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ScaniiClientTest {
  private static final String KEY = System.getenv("TEST_KEY");
  private static final String SECRET = System.getenv("TEST_SECRET");
  private final Path eicarFile;
  private ScaniiClient client;

  public ScaniiClientTest() throws IOException {
    this.eicarFile = Files.write(Files.createTempFile(null, null), EICAR.SIGNATURE.getBytes());
  }

  @Before
  public void before() {
    client = new ScaniiClient(ScaniiTarget.latest(), KEY, SECRET);
  }

  @Test
  public void testProcess() throws Exception {

    ScaniiResult result;
    // simple processing clean
    result = client.process(Systems.randomFile(1024));
    assertNotNull(result.getResourceId());
    assertNotNull(result.getChecksum());
    assertNotNull(result.getResourceLocation());
    assertNotNull(result.getRawResponse());
    assertNotNull(result.getRequestId());
    assertNotNull(result.getContentType());
    assertNotNull(result.getHostId());
    assertNotNull(result.getFindings());
    assertTrue(result.getFindings().isEmpty());
    System.out.println(result);

    // with findings
    result = client.process(eicarFile);
    assertNotNull(result.getResourceId());
    assertNotNull(result.getChecksum());
    assertNotNull(result.getResourceLocation());
    assertNotNull(result.getRawResponse());
    assertNotNull(result.getRequestId());
    assertNotNull(result.getContentType());
    assertNotNull(result.getHostId());
    assertNotNull(result.getFindings());
    assertTrue(result.getFindings().size() == 1);
    assertEquals("content.malicious.eicar-test-signature", result.getFindings().get(0));
    System.out.println(result);

    // failures:

  }

  @Test
  public void testProcessWithMetadata() throws Exception {

    ScaniiResult result;

    // simple processing clean
    result = client.process(Systems.randomFile(1024), ImmutableMap.of("foo", "bar"));
    assertNotNull(result.getResourceId());
    assertEquals("bar", result.getMetadata().get("foo"));
    System.out.println(result);
  }


  @Test(expected = ScaniiException.class)
  public void shouldThrowErrorsIfInvalidPost() throws IOException {

    // empty file:
    client.process(Files.createTempFile(null, null));

  }

  @Test(expected = ScaniiException.class)
  public void shouldThrowErrorsIfInvalidCredentials() throws IOException {
    ScaniiClient client = new ScaniiClient(ScaniiTarget.v2_0, "foo", "bar");

    // empty file:
    client.process(Files.createTempFile(null, null));

  }

  @Test
  public void testProcessAsync() throws Exception {

    ScaniiResult result;
    // simple processing clean
    result = client.processAsync(Systems.randomFile(1024));
    assertNotNull(result.getResourceId());
    assertNull(result.getChecksum());
    assertNotNull(result.getResourceLocation());
    assertNotNull(result.getRawResponse());
    assertNotNull(result.getRequestId());
    assertNull(result.getContentType());
    assertNotNull(result.getHostId());
    assertNull(result.getFindings());
    System.out.println(result);

    Thread.sleep(1000);

    // now fetching the retrieve
    ScaniiResult actualResult = client.retrieve(result.getResourceId());
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
  public void testProcessAsyncWithMetadata() throws Exception {

    ScaniiResult result;

    // simple processing clean
    result = client.processAsync(Systems.randomFile(1024), ImmutableMap.of("foo", "bar"));
    Thread.sleep(1000);

    // now fetching the retrieve
    ScaniiResult actualResult = client.retrieve(result.getResourceId());
    assertNotNull(actualResult.getResourceId());
    assertEquals("bar", actualResult.getMetadata().get("foo"));
  }

  @Test
  public void testFetchWithoutCallback() throws InterruptedException {

    ScaniiResult result;
    // simple processing clean
    result = client.fetch("https://scanii.s3.amazonaws.com/eicarcom2.zip");
    assertNotNull(result.getResourceId());
    assertNull(result.getChecksum());
    assertNotNull(result.getResourceLocation());
    assertNotNull(result.getRawResponse());
    assertNotNull(result.getRequestId());
    assertNull(result.getContentType());
    assertNotNull(result.getHostId());
    assertNull(result.getFindings());
    System.out.println(result);

    Thread.sleep(1000);

    // fetching result:
    ScaniiResult actualResult = client.retrieve(result.getResourceId());
    assertNotNull(actualResult.getResourceId());
    assertNotNull(actualResult.getChecksum());
    assertNull(actualResult.getResourceLocation());
    assertNotNull(actualResult.getRawResponse());
    assertNotNull(actualResult.getRequestId());
    assertNotNull(actualResult.getContentType());
    assertNotNull(actualResult.getHostId());
    assertNotNull(actualResult.getFindings());
    assertEquals("content.malicious.eicar-test-signature", actualResult.getFindings().get(0));
  }

  @Test
  public void testFetchWithCallback() throws InterruptedException {

    ScaniiResult result;
    // simple processing clean
    result = client.fetch("https://scanii.s3.amazonaws.com/eicarcom2.zip", "http://google.com");
    assertNotNull(result.getResourceId());
    assertNull(result.getChecksum());
    assertNotNull(result.getResourceLocation());
    assertNotNull(result.getRawResponse());
    assertNotNull(result.getRequestId());
    assertNull(result.getContentType());
    assertNotNull(result.getHostId());
    assertNull(result.getFindings());
    System.out.println(result);

    Thread.sleep(1000);

    // fetching result:
    ScaniiResult actualResult = client.retrieve(result.getResourceId());
    assertNotNull(actualResult.getResourceId());
    assertNotNull(actualResult.getChecksum());
    assertNull(actualResult.getResourceLocation());
    assertNotNull(actualResult.getRawResponse());
    assertNotNull(actualResult.getRequestId());
    assertNotNull(actualResult.getContentType());
    assertNotNull(actualResult.getHostId());
    assertNotNull(actualResult.getFindings());
    assertEquals("content.malicious.eicar-test-signature", actualResult.getFindings().get(0));
  }

  @Test
  public void testFetchWithMetadata() throws Exception {

    ScaniiResult result = client.fetch("https://scanii.s3.amazonaws.com/eicarcom2.zip", "http://google.com", ImmutableMap.of("foo", "bar"));
    assertNotNull(result.getResourceId());
    Thread.sleep(1000);

    // fetching result:
    ScaniiResult actualResult = client.retrieve(result.getResourceId());
    assertEquals("bar", actualResult.getMetadata().get("foo"));
    System.out.println(result);
  }

  @Test
  public void testPing() throws Exception {
    assertTrue(client.ping());
  }

  @Test
  public void testCreateAuthToken() throws Exception {
    ScaniiResult result = client.createAuthToken(1, TimeUnit.HOURS);
    assertNotNull(result.getResourceId());
    assertNotNull(result.getExpirationDate());
    assertNotNull(result.getCreationDate());

    Thread.sleep(1000);

    // now using the auth token to create a new client and process content
    ScaniiClient tempClient = new ScaniiClient(ScaniiTarget.latest(), result.getResourceId(), null);
    result = tempClient.process(Systems.randomFile(1024));
    assertNotNull(result.getResourceId());
    assertNotNull(result.getChecksum());
    assertNotNull(result.getResourceLocation());
    assertNotNull(result.getRawResponse());
    assertNotNull(result.getRequestId());
    assertNotNull(result.getContentType());
    assertNotNull(result.getHostId());
    assertNotNull(result.getFindings());
    assertTrue(result.getFindings().isEmpty());
    System.out.println(result);


  }

  @Test
  public void testDeleteAuthToken() throws Exception {
    ScaniiResult result = client.createAuthToken(1, TimeUnit.HOURS);
    client.deleteAuthToken(result.getResourceId());
  }

  @Test
  public void testRetrieveAuthToken() throws Exception {
    ScaniiResult result = client.createAuthToken(1, TimeUnit.HOURS);
    ScaniiResult result2 = client.retrieveAuthToken(result.getResourceId());
    assertEquals(result.getResourceId(), result2.getResourceId());
    assertEquals(result.getCreationDate(), result2.getCreationDate());
    assertEquals(result.getExpirationDate(), result2.getExpirationDate());
  }

  @Test
  @Ignore("slow test")
  public void shouldHandleLargeFiles() throws IOException {
    Path f = Systems.randomFile(104857600);
    client.process(f);
  }

  @Test
  public void shouldPingAllRegions() {
    for (ScaniiTarget target : ScaniiTarget.values()) {
      System.out.println(target);
      ScaniiClient client = new ScaniiClient(target, KEY, SECRET);
      Assert.assertTrue(client.ping());
    }
  }
}
