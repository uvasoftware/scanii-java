package com.uvasoftware.scanii.batch;

import com.uvasoftware.scanii.ScaniiClient;
import com.uvasoftware.scanii.internal.Loggers;
import com.uvasoftware.scanii.models.ScaniiProcessingResult;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * High performance batch client for concurrently processing lots of files
 */
public class ScaniiBatchClient {
  public static final int MAX_CONCURRENT_REQUESTS = 8 * Runtime.getRuntime().availableProcessors();
  private static final Logger LOG = Loggers.build();
  private final Semaphore semaphore;

  private final ExecutorService workers;
  private final ScaniiClient client;
  private final AtomicInteger pending = new AtomicInteger();
  private final AtomicLong completed = new AtomicLong();
  private final AtomicLong failed = new AtomicLong();

  public ScaniiBatchClient(ScaniiClient client) {
    this(client, MAX_CONCURRENT_REQUESTS);
  }

  public ScaniiBatchClient(ScaniiClient client, int maxConcurrentRequests) {
    this.client = client;
    semaphore = new Semaphore(maxConcurrentRequests);
    workers = Executors.newWorkStealingPool(maxConcurrentRequests);
    LOG.info("batch client created with {} max concurrent requests", maxConcurrentRequests);
  }

  /**
   * Submits a file for batch processing
   *
   * @param content Path to the content to be processed
   * @param handler Method to be called once processing is completed and a result is at hand
   */
  public void submit(final Path content, final Consumer<ScaniiProcessingResult> handler) {
    submit(content, handler, Throwable::printStackTrace);
  }

  /**
   * Submits a file for batch processing
   *
   * @param content          Path to the content to be processed
   * @param handler          Method to be called once processing is completed and a result is at hand
   * @param exceptionHandler Method to be called once an exception is thrown
   */
  public void submit(final Path content, final Consumer<ScaniiProcessingResult> handler, final Consumer<Throwable> exceptionHandler) {
    try {
      semaphore.acquire();
      pending.incrementAndGet();
      workers.execute(() -> {
        String originalThreadName = Thread.currentThread().getName();
        Thread.currentThread().setName(String.format("ScaniiBatchWorker[%s]", content.getFileName()));
        try {
          ScaniiProcessingResult result = client.process(content);
          completed.incrementAndGet();
          handler.accept(result);
        } finally {
          Thread.currentThread().setName(originalThreadName);
          pending.decrementAndGet();
        }
      });
    } catch (Exception ex) {
      failed.incrementAndGet();
      exceptionHandler.accept(ex);
    } finally {
      semaphore.release();
    }
  }

  public boolean hasPending() {
    return pending.get() > 0;
  }

  public long getCompletedCount() {
    return completed.get();
  }

  public long getFailedCount() {
    return failed.get();
  }

  public ScaniiClient getClient() {
    return client;
  }
}
