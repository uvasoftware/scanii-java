package com.uvasoftware.scanii.misc;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class Threads {
  public static void waitUntil(BooleanSupplier supplier, int timeout, TimeUnit unit, int sleep, TimeUnit sleepUnit) {
    Instant limit = Instant.now().plusMillis(unit.toMillis(timeout));
    while (true) {
      if (Instant.now().isAfter(limit)) {
        throw new IllegalStateException(String.format("timed out waiting on event %s", supplier));
      }
      if (supplier.getAsBoolean()) {
        return;
      }
      try {
        Thread.sleep(sleepUnit.toMillis(sleep));
      } catch (InterruptedException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  public static void waitUntil(BooleanSupplier supplier, int timeout, TimeUnit unit) {
    waitUntil(supplier, timeout, unit, 200, TimeUnit.MILLISECONDS);
  }
}
