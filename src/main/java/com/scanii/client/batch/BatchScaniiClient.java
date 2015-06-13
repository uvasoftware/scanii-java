package com.scanii.client.batch;

import com.scanii.client.ScaniiClient;
import com.scanii.client.ScaniiException;
import com.scanii.client.ScaniiResult;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * High performance client wrapper for handling large number of files
 */
public class BatchScaniiClient {
  private static final int MAX_CONCURRENT_REQUESTS = 10;
  private final Semaphore pending;

  private final ExecutorService workers;

  private final ScaniiClient client;

  public BatchScaniiClient(ScaniiClient client) {
    this(client, MAX_CONCURRENT_REQUESTS);
  }

  public BatchScaniiClient(ScaniiClient client, int maxConcurrentRequests) {
    this.client = client;
    pending = new Semaphore(maxConcurrentRequests);
    workers = Executors.newWorkStealingPool(maxConcurrentRequests);
  }

  /**
   * Submits a file for batch processing
   * @param content Path to the content to be processed
   * @param handler Method to be called once processing is completed and a result is at hand
   */
  public void submit(final Path content, final ScaniiResultHandler handler) {
    try {
      pending.acquire();
      workers.execute(new Runnable() {
        @Override
        public void run() {
          String originalThreadName = Thread.currentThread().getName();
          Thread.currentThread().setName("ScaniiBatchWorker");
          try {
            ScaniiResult result = client.process(content);
            handler.handle(result);

          } finally {
            Thread.currentThread().setName(originalThreadName);
          }
        }
      });
    } catch (Exception ex) {
      throw new ScaniiException(ex);
    } finally {
      pending.release();
    }
  }
}
