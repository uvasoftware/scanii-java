package com.uvasoftware.scanii.misc;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class Threads {
  public static void waitUntil(BooleanSupplier supplier, int timeout, TimeUnit unit, int sleep, TimeUnit sleepUnit) {
    Stopwatch sw = Stopwatch.createStarted();
    while (true) {
      Preconditions.checkState(sw.elapsed(unit) < timeout, "timed out waiting on event %s", supplier);
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
