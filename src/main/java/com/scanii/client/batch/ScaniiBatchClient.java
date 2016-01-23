package com.scanii.client.batch;

import com.scanii.client.ScaniiClient;
import com.scanii.client.ScaniiException;
import com.scanii.client.ScaniiResult;
import com.scanii.client.misc.Loggers;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * High performance batch client for concurrently processing lots of files
 */
public class ScaniiBatchClient {
  private static final Logger LOG = Loggers.build();
  private static final int MAX_CONCURRENT_REQUESTS = 10 * Runtime.getRuntime().availableProcessors();
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
  public void submit(final Path content, final ScaniiResultHandler handler) {
    try {
      semaphore.acquire();
      pending.incrementAndGet();
      workers.execute(new Runnable() {
        @Override
        public void run() {
          String originalThreadName = Thread.currentThread().getName();
          Thread.currentThread().setName(String.format("ScaniiBatchWorker[%s]", content.getFileName()));
          try {
            ScaniiResult result = client.process(content);
            completed.incrementAndGet();
            handler.handle(result);
          } finally {
            Thread.currentThread().setName(originalThreadName);
            pending.decrementAndGet();
          }
        }
      });
    } catch (Exception ex) {
      failed.incrementAndGet();
      throw new ScaniiException(ex);
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
}
