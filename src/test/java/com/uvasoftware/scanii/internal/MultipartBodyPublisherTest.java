package com.uvasoftware.scanii.internal;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MultipartBodyPublisherTest {

  private static String bodyToString(HttpRequest.BodyPublisher publisher) {
    byte[] bytes = new byte[(int) publisher.contentLength()];
    publisher.subscribe(new java.util.concurrent.Flow.Subscriber<>() {
      int offset = 0;
      java.util.concurrent.Flow.Subscription sub;

      @Override
      public void onSubscribe(java.util.concurrent.Flow.Subscription subscription) {
        this.sub = subscription;
        subscription.request(Long.MAX_VALUE);
      }

      @Override
      public void onNext(java.nio.ByteBuffer item) {
        int len = item.remaining();
        item.get(bytes, offset, len);
        offset += len;
      }

      @Override
      public void onError(Throwable throwable) {
        throw new RuntimeException(throwable);
      }

      @Override
      public void onComplete() {
      }
    });
    return new String(bytes, StandardCharsets.UTF_8);
  }

  @Test
  void contentTypeShouldContainBoundary() {
    MultipartBodyPublisher pub = new MultipartBodyPublisher();
    String ct = pub.contentType();
    assertTrue(ct.startsWith("multipart/form-data; boundary="));
    // boundary should be non-empty after the prefix
    String boundary = ct.substring("multipart/form-data; boundary=".length());
    assertFalse(boundary.isEmpty());
  }

  @Test
  void shouldBuildTextPart() {
    MultipartBodyPublisher pub = new MultipartBodyPublisher();
    pub.addTextBody("callback", "https://example.com/hook");
    String body = bodyToString(pub.build());

    String boundary = pub.contentType().substring("multipart/form-data; boundary=".length());

    assertTrue(body.contains("--" + boundary + "\r\n"));
    assertTrue(body.contains("Content-Disposition: form-data; name=\"callback\""));
    assertTrue(body.contains("Content-Type: text/plain; charset=UTF-8"));
    assertTrue(body.contains("https://example.com/hook"));
    assertTrue(body.endsWith("--" + boundary + "--\r\n"));
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

      assertTrue(body.contains("Content-Disposition: form-data; name=\"file\"; filename=\"" + tmp.getFileName() + "\""));
      assertTrue(body.contains("Content-Type: application/octet-stream"));
      assertTrue(body.contains("file-content-here"));
      assertTrue(body.endsWith("--" + boundary + "--\r\n"));
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

    assertTrue(body.contains("Content-Disposition: form-data; name=\"file\"; filename=\"upload\""));
    assertTrue(body.contains("Content-Type: application/octet-stream"));
    assertTrue(body.contains("stream-data"));
    assertTrue(body.endsWith("--" + boundary + "--\r\n"));
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
      assertTrue(body.contains("name=\"file\""));
      assertTrue(body.contains("binary-data"));
      assertTrue(body.contains("name=\"metadata[tag]\""));
      assertTrue(body.contains("important"));
      assertTrue(body.contains("name=\"callback\""));
      assertTrue(body.contains("https://example.com"));

      // should have exactly 3 part boundaries + 1 closing boundary
      int partCount = countOccurrences(body, "--" + boundary + "\r\n");
      assertEquals(3, partCount);
      assertTrue(body.endsWith("--" + boundary + "--\r\n"));
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
    assertSame(pub, result);
  }

  @Test
  void shouldSupportFluentChainingForBinaryStream() {
    MultipartBodyPublisher pub = new MultipartBodyPublisher();
    MultipartBodyPublisher result = pub.addBinaryBody("file", new ByteArrayInputStream(new byte[0]));
    assertSame(pub, result);
  }

  @Test
  void shouldSupportFluentChainingForBinaryPath() throws IOException {
    Path tmp = Files.createTempFile("chain-test", ".bin");
    try {
      MultipartBodyPublisher pub = new MultipartBodyPublisher();
      MultipartBodyPublisher result = pub.addBinaryBody("file", tmp);
      assertSame(pub, result);
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

      assertTrue(body.contains("name=\"file\""));
      // binary section should have the headers but no file content between them and the boundary
      String boundary = pub.contentType().substring("multipart/form-data; boundary=".length());
      assertTrue(body.contains("Content-Type: application/octet-stream\r\n\r\n\r\n"));
      assertTrue(body.endsWith("--" + boundary + "--\r\n"));
    } finally {
      Files.deleteIfExists(tmp);
    }
  }

  @Test
  void shouldHandleEmptyInputStream() {
    MultipartBodyPublisher pub = new MultipartBodyPublisher();
    pub.addBinaryBody("file", new ByteArrayInputStream(new byte[0]));
    String body = bodyToString(pub.build());

    assertTrue(body.contains("name=\"file\""));
    assertTrue(body.contains("Content-Type: application/octet-stream\r\n\r\n\r\n"));
  }

  @Test
  void contentLengthShouldBePositive() {
    MultipartBodyPublisher pub = new MultipartBodyPublisher();
    pub.addTextBody("key", "value");
    HttpRequest.BodyPublisher bp = pub.build();
    assertTrue(bp.contentLength() > 0);
  }

  @Test
  void eachInstanceShouldHaveUniqueBoundary() {
    MultipartBodyPublisher a = new MultipartBodyPublisher();
    MultipartBodyPublisher b = new MultipartBodyPublisher();
    assertNotEquals(a.contentType(), b.contentType());
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
