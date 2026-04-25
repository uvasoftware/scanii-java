package com.scanii;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

abstract class IntegrationTest {
  private static final System.Logger LOG = System.getLogger(IntegrationTest.class.getName());
  protected  <T> T pollForResult(Supplier<T> supplier, Duration timeout) throws InterruptedException {
    Instant limit = Instant.now().plus(timeout);
    while (Instant.now().isBefore(limit)) {
      if (supplier.get() != null) {
        return supplier.get();
      }
      LOG.log(System.Logger.Level.INFO, "waiting...");
      //noinspection BusyWait
      Thread.sleep(Duration.ofSeconds(1).toMillis());
    }
    throw new IllegalStateException(String.format("timed out waiting on event %s", supplier));

  }
}
