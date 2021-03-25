package com.uvasoftware.scanii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

abstract class IntegrationTest {
  private static final Logger LOG = LoggerFactory.getLogger(IntegrationTest.class);
  protected  <T> T pollForResult(Supplier<T> supplier, Duration timeout) throws InterruptedException {
    Instant limit = Instant.now().plus(timeout);
    while (Instant.now().isBefore(limit)) {
      if (supplier.get() != null) {
        return supplier.get();
      }
      LOG.info("waiting...");
      //noinspection BusyWait
      Thread.sleep(Duration.ofSeconds(1).toMillis());
    }
    throw new IllegalStateException(String.format("timed out waiting on event %s", supplier));

  }
}
