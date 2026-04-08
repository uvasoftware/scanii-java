package com.uvasoftware.scanii.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

class MultipartBodyPublisher {
  private final String boundary;
  private final ByteArrayOutputStream out;

  MultipartBodyPublisher() {
    this.boundary = UUID.randomUUID().toString();
    this.out = new ByteArrayOutputStream();
  }

  String contentType() {
    return "multipart/form-data; boundary=" + boundary;
  }

  MultipartBodyPublisher addTextBody(String name, String value) {
    writeString("--" + boundary + "\r\n");
    writeString("Content-Disposition: form-data; name=\"" + name + "\"\r\n");
    writeString("Content-Type: text/plain; charset=UTF-8\r\n\r\n");
    writeString(value + "\r\n");
    return this;
  }

  MultipartBodyPublisher addBinaryBody(String name, Path file) {
    try {
      writeString("--" + boundary + "\r\n");
      writeString("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + file.getFileName() + "\"\r\n");
      writeString("Content-Type: application/octet-stream\r\n\r\n");
      out.write(Files.readAllBytes(file));
      writeString("\r\n");
      return this;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  MultipartBodyPublisher addBinaryBody(String name, InputStream stream) {
    try {
      writeString("--" + boundary + "\r\n");
      writeString("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"upload\"\r\n");
      writeString("Content-Type: application/octet-stream\r\n\r\n");
      out.write(stream.readAllBytes());
      writeString("\r\n");
      return this;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  HttpRequest.BodyPublisher build() {
    writeString("--" + boundary + "--\r\n");
    return HttpRequest.BodyPublishers.ofByteArray(out.toByteArray());
  }

  private void writeString(String s) {
    try {
      out.write(s.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
