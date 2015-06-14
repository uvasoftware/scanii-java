package com.scanii.client.batch;

import com.scanii.client.ScaniiResult;

/**
 * Pre lambda functional interface that acts upon an async processing retrieve
 */
public interface ScaniiResultHandler {
  void handle(ScaniiResult result);
}
