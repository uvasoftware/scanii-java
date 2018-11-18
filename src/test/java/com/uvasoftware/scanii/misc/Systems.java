package com.uvasoftware.scanii.misc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;

public class Systems {
  private static final SecureRandom random = new SecureRandom();

  public static String gibberish(int length) {
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length) {
      sb.append(Integer.toHexString(random.nextInt()));
    }
    return sb.toString().substring(0, length);
  }

  public static Path randomFile(long size) throws IOException {

    long offset = 0;
    Path fd = Files.createTempFile(null, null);
    while (offset < size) {
      long stride = size - offset < 1000000 ? ((int) size - offset) : 1000000;
      Files.write(fd, gibberish((int) stride).getBytes(Charset.defaultCharset()), StandardOpenOption.APPEND);
      offset += stride;
    }
    return fd;
  }
}
