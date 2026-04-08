package com.uvasoftware.scanii.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

@Execution(ExecutionMode.CONCURRENT)
class MultipartBodyPublisherTest {

  private static String bodyToString(HttpRequest.BodyPublisher publisher) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    CountDownLatch latch = new CountDownLatch(1);

    publisher.subscribe(new Flow.Subscriber<>() {
      Flow.Subscription sub;

      @Override
      public void onSubscribe(Flow.Subscription subscription) {
        this.sub = subscription;
        subscription.request(Long.MAX_VALUE);
      }

      @Override
      public void onNext(ByteBuffer item) {
        byte[] bytes = new byte[item.remaining()];
        item.get(bytes);
        out.write(bytes, 0, bytes.length);
      }

      @Override
      public void onError(Throwable throwable) {
        throw new RuntimeException(throwable);
      }

      @Override
      public void onComplete() {
        latch.countDown();
      }
    });

    try {
      Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS), "Timed out waiting for body publisher");
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return out.toString(StandardCharsets.UTF_8);
  }

  @Test
  void contentTypeShouldContainBoundary() {
    MultipartBodyPublisher pub = new MultipartBodyPublisher();
    String ct = pub.contentType();
    Assertions.assertTrue(ct.startsWith("multipart/form-data; boundary="));
    // boundary should be non-empty after the prefix
    String boundary = ct.substring("multipart/form-data; boundary=".length());
    Assertions.assertFalse(boundary.isEmpty());
  }

  @Test
  void shouldBuildTextPart() {
    MultipartBodyPublisher pub = new MultipartBodyPublisher();
    pub.addTextBody("callback", "https://example.com/hook");
    String body = bodyToString(pub.build());

    String boundary = pub.contentType().substring("multipart/form-data; boundary=".length());

    Assertions.assertTrue(body.contains("--" + boundary + "\r\n"));
    Assertions.assertTrue(body.contains("Content-Disposition: form-data; name=\"callback\""));
    Assertions.assertTrue(body.contains("Content-Type: text/plain; charset=UTF-8"));
    Assertions.assertTrue(body.contains("https://example.com/hook"));
    Assertions.assertTrue(body.endsWith("--" + boundary + "--\r\n"));
  }

  @Test
  void shouldBuildBinaryPartFromPath() throws IOException {
    Path tmp = Files.createTempFile("multipart-test", ".bin");
    try {
      byte[] content = "file-content-here".getBytes(StandardCharsets.UTF_8);
      Files.write(tmp, content);

      MultipartBodyPublisher pub = new MultipartBodyPublisher();
      pub.addBinaryBody("file", tmp);
      String body = bodyToString(pub.build());

      String boundary = pub.contentType().substring("multipart/form-data; boundary=".length());

      Assertions.assertTrue(body.contains("Content-Disposition: form-data; name=\"file\"; filename=\"" + tmp.getFileName() + "\""));
      Assertions.assertTrue(body.contains("Content-Type: application/octet-stream"));
      Assertions.assertTrue(body.contains("file-content-here"));
      Assertions.assertTrue(body.endsWith("--" + boundary + "--\r\n"));
    } finally {
      Files.deleteIfExists(tmp);
    }
  }

  @Test
  void shouldBuildBinaryPartFromInputStream() {
    byte[] content = "stream-data".getBytes(StandardCharsets.UTF_8);
    InputStream is = new ByteArrayInputStream(content);

    MultipartBodyPublisher pub = new MultipartBodyPublisher();
    pub.addBinaryBody("file", is);
    String body = bodyToString(pub.build());

    String boundary = pub.contentType().substring("multipart/form-data; boundary=".length());

    Assertions.assertTrue(body.contains("Content-Disposition: form-data; name=\"file\"; filename=\"upload\""));
    Assertions.assertTrue(body.contains("Content-Type: application/octet-stream"));
    Assertions.assertTrue(body.contains("stream-data"));
    Assertions.assertTrue(body.endsWith("--" + boundary + "--\r\n"));
  }

  @Test
  void shouldBuildMultipleParts() throws IOException {
    Path tmp = Files.createTempFile("multipart-multi", ".txt");
    try {
      Files.write(tmp, "binary-data".getBytes(StandardCharsets.UTF_8));

      MultipartBodyPublisher pub = new MultipartBodyPublisher();
      pub.addBinaryBody("file", tmp);
      pub.addTextBody("metadata[tag]", "important");
      pub.addTextBody("callback", "https://example.com");
      String body = bodyToString(pub.build());

      String boundary = pub.contentType().substring("multipart/form-data; boundary=".length());

      // all three parts should be present
      Assertions.assertTrue(body.contains("name=\"file\""));
      Assertions.assertTrue(body.contains("binary-data"));
      Assertions.assertTrue(body.contains("name=\"metadata[tag]\""));
      Assertions.assertTrue(body.contains("important"));
      Assertions.assertTrue(body.contains("name=\"callback\""));
      Assertions.assertTrue(body.contains("https://example.com"));

      // should have exactly 3 part boundaries + 1 closing boundary
      int partCount = countOccurrences(body, "--" + boundary + "\r\n");
      Assertions.assertEquals(3, partCount);
      Assertions.assertTrue(body.endsWith("--" + boundary + "--\r\n"));
    } finally {
      Files.deleteIfExists(tmp);
    }
  }

  @Test
  void shouldSupportFluentChaining() {
    MultipartBodyPublisher pub = new MultipartBodyPublisher();
    MultipartBodyPublisher result = pub
      .addTextBody("a", "1")
      .addTextBody("b", "2");
    Assertions.assertSame(pub, result);
  }

  @Test
  void shouldSupportFluentChainingForBinaryStream() {
    MultipartBodyPublisher pub = new MultipartBodyPublisher();
    MultipartBodyPublisher result = pub.addBinaryBody("file", new ByteArrayInputStream(new byte[0]));
    Assertions.assertSame(pub, result);
  }

  @Test
  void shouldSupportFluentChainingForBinaryPath() throws IOException {
    Path tmp = Files.createTempFile("chain-test", ".bin");
    try {
      MultipartBodyPublisher pub = new MultipartBodyPublisher();
      MultipartBodyPublisher result = pub.addBinaryBody("file", tmp);
      Assertions.assertSame(pub, result);
    } finally {
      Files.deleteIfExists(tmp);
    }
  }

  @Test
  void shouldHandleEmptyFilePath() throws IOException {
    Path tmp = Files.createTempFile("empty-file", ".bin");
    try {
      // file is empty (0 bytes)
      MultipartBodyPublisher pub = new MultipartBodyPublisher();
      pub.addBinaryBody("file", tmp);
      String body = bodyToString(pub.build());

      Assertions.assertTrue(body.contains("name=\"file\""));
      // binary section should have the headers but no file content between them and the boundary
      String boundary = pub.contentType().substring("multipart/form-data; boundary=".length());
      Assertions.assertTrue(body.contains("Content-Type: application/octet-stream\r\n\r\n\r\n"));
      Assertions.assertTrue(body.endsWith("--" + boundary + "--\r\n"));
    } finally {
      Files.deleteIfExists(tmp);
    }
  }

  @Test
  void shouldHandleEmptyInputStream() {
    MultipartBodyPublisher pub = new MultipartBodyPublisher();
    pub.addBinaryBody("file", new ByteArrayInputStream(new byte[0]));
    String body = bodyToString(pub.build());

    Assertions.assertTrue(body.contains("name=\"file\""));
    Assertions.assertTrue(body.contains("Content-Type: application/octet-stream\r\n\r\n\r\n"));
  }

  @Test
  void shouldStreamWithoutKnownContentLength() {
    MultipartBodyPublisher pub = new MultipartBodyPublisher();
    pub.addTextBody("key", "value");
    HttpRequest.BodyPublisher bp = pub.build();
    // streaming publisher reports unknown content length
    Assertions.assertEquals(-1, bp.contentLength());
  }

  @Test
  void eachInstanceShouldHaveUniqueBoundary() {
    MultipartBodyPublisher a = new MultipartBodyPublisher();
    MultipartBodyPublisher b = new MultipartBodyPublisher();
    Assertions.assertNotEquals(a.contentType(), b.contentType());
  }

  private static int countOccurrences(String haystack, String needle) {
    int count = 0;
    int idx = 0;
    while ((idx = haystack.indexOf(needle, idx)) != -1) {
      count++;
      idx += needle.length();
    }
    return count;
  }
}
