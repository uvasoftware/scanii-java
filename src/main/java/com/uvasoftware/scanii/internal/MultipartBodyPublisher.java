package com.uvasoftware.scanii.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

class MultipartBodyPublisher {
  private final String boundary;
  private final List<Supplier<InputStream>> parts;

  MultipartBodyPublisher() {
    this.boundary = UUID.randomUUID().toString();
    this.parts = new ArrayList<>();
  }

  String contentType() {
    return "multipart/form-data; boundary=" + boundary;
  }

  MultipartBodyPublisher addTextBody(String name, String value) {
    byte[] bytes = ("--" + boundary + "\r\n"
      + "Content-Disposition: form-data; name=\"" + name + "\"\r\n"
      + "Content-Type: text/plain; charset=UTF-8\r\n\r\n"
      + value + "\r\n").getBytes(StandardCharsets.UTF_8);
    parts.add(() -> new ByteArrayInputStream(bytes));
    return this;
  }

  MultipartBodyPublisher addBinaryBody(String name, Path file) {
    byte[] header = ("--" + boundary + "\r\n"
      + "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + file.getFileName() + "\"\r\n"
      + "Content-Type: application/octet-stream\r\n\r\n").getBytes(StandardCharsets.UTF_8);
    byte[] trailer = "\r\n".getBytes(StandardCharsets.UTF_8);
    parts.add(() -> new ByteArrayInputStream(header));
    parts.add(() -> {
      try {
        return Files.newInputStream(file);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });
    parts.add(() -> new ByteArrayInputStream(trailer));
    return this;
  }

  MultipartBodyPublisher addBinaryBody(String name, InputStream stream) {
    byte[] header = ("--" + boundary + "\r\n"
      + "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"upload\"\r\n"
      + "Content-Type: application/octet-stream\r\n\r\n").getBytes(StandardCharsets.UTF_8);
    byte[] trailer = "\r\n".getBytes(StandardCharsets.UTF_8);
    parts.add(() -> new ByteArrayInputStream(header));
    parts.add(() -> stream);
    parts.add(() -> new ByteArrayInputStream(trailer));
    return this;
  }

  HttpRequest.BodyPublisher build() {
    byte[] closing = ("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);
    parts.add(() -> new ByteArrayInputStream(closing));

    Iterator<Supplier<InputStream>> iterator = parts.iterator();
    @SuppressWarnings("resource")
    InputStream combined = new SequenceInputStream(new Enumeration<>() {
      @Override
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }

      @Override
      public InputStream nextElement() {
        return iterator.next().get();
      }
    });

    return HttpRequest.BodyPublishers.ofInputStream(() -> combined);
  }
}
