package com.scanii.client;

import com.scanii.client.misc.EICAR;
import com.scanii.client.misc.Systems;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class ScaniiClientTest {
  private static final String KEY = System.getenv("TEST_KEY");
  private static final String SECRET = System.getenv("TEST_SECRET");
  private final Path eicarFile;

  public ScaniiClientTest() throws IOException {
    this.eicarFile = Files.write(Files.createTempFile(null, null), EICAR.SIGNATURE.getBytes());
  }

  @Test
  public void testProcess() throws Exception {
    ScaniiClient client = new ScaniiClient(ScaniiTarget.v2_0, KEY, SECRET);

    ScaniiResult result;
    // simple processing clean
    result = client.process(Systems.randomFile(1024));
    assertNotNull(result.getFileId());
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
    assertNotNull(result.getFileId());
    assertNotNull(result.getChecksum());
    assertNotNull(result.getResourceLocation());
    assertNotNull(result.getRawResponse());
    assertNotNull(result.getRequestId());
    assertNotNull(result.getContentType());
    assertNotNull(result.getHostId());
    assertNotNull(result.getFindings());
    assertTrue(result.getFindings().size() == 1);
    assertEquals("av.eicar-test-signature", result.getFindings().get(0));
    System.out.println(result);

    // failures:

  }

  @Test(expected = ScaniiException.class)
  public void shoudlThrowErrosIfInvalidPost() throws IOException {
    ScaniiClient client = new ScaniiClient(ScaniiTarget.v2_0, KEY, SECRET);

    // empty file:
    client.process(Files.createTempFile(null, null));

  }

  @Test(expected = ScaniiException.class)
  public void shoudlThrowErrosIfInvalidCredentials() throws IOException {
    ScaniiClient client = new ScaniiClient(ScaniiTarget.v2_0, "foo", "bar");

    // empty file:
    client.process(Files.createTempFile(null, null));

  }

  @Test
  public void testProcessAsync() throws Exception {
    ScaniiClient client = new ScaniiClient(ScaniiTarget.v2_0, KEY, SECRET);

    ScaniiResult result;
    // simple processing clean
    result = client.processAsync(Systems.randomFile(1024));
    assertNotNull(result.getFileId());
    assertNull(result.getChecksum());
    assertNotNull(result.getResourceLocation());
    assertNotNull(result.getRawResponse());
    assertNotNull(result.getRequestId());
    assertNull(result.getContentType());
    assertNotNull(result.getHostId());
    assertNull(result.getFindings());
    System.out.println(result);

    Thread.sleep(5000);

    // now fetching the result
    ScaniiResult actualResult = client.result(result.getFileId());
    assertNotNull(actualResult.getFileId());
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
  public void testFetchWithoutCallback() throws InterruptedException {
    ScaniiClient client = new ScaniiClient(ScaniiTarget.v2_0, KEY, SECRET);

    ScaniiResult result;
    // simple processing clean
    result = client.fetch("https://scanii.s3.amazonaws.com/eicarcom2.zip");
    assertNotNull(result.getFileId());
    assertNull(result.getChecksum());
    assertNotNull(result.getResourceLocation());
    assertNotNull(result.getRawResponse());
    assertNotNull(result.getRequestId());
    assertNull(result.getContentType());
    assertNotNull(result.getHostId());
    assertNull(result.getFindings());
    System.out.println(result);

    Thread.sleep(5000);

    // fetching result:
    ScaniiResult actualResult = client.result(result.getFileId());
    assertNotNull(actualResult.getFileId());
    assertNotNull(actualResult.getChecksum());
    assertNull(actualResult.getResourceLocation());
    assertNotNull(actualResult.getRawResponse());
    assertNotNull(actualResult.getRequestId());
    assertNotNull(actualResult.getContentType());
    assertNotNull(actualResult.getHostId());
    assertNotNull(actualResult.getFindings());
    assertEquals("av.eicar-test-signature", actualResult.getFindings().get(0));
  }
}
