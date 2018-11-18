package com.uvasoftware.scanii;

import com.uvasoftware.scanii.batch.ScaniiBatchClient;
import com.uvasoftware.scanii.impl.DefaultScaniiClient;
import com.uvasoftware.scanii.internal.Loggers;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

public class ScaniiClients {
  public static final String VERSION;
  private static final Logger LOG = Loggers.build();

  static {
    if (ScaniiClients.class.getPackage().getImplementationVersion() != null) {
      VERSION = ScaniiClients.class.getPackage().getImplementationVersion();
    } else {
      VERSION = "0.0-dev";
    }

  }

  public static ScaniiClient createDefault(ScaniiTarget target, String authToken) {
    return new DefaultScaniiClient(target, authToken, "", HttpClients.createDefault());
  }

  public static ScaniiClient createDefault(ScaniiTarget target, String key, String secret) {
    return new DefaultScaniiClient(target, key, secret, HttpClients.createDefault());
  }

  public static ScaniiClient createDefault(ScaniiTarget target, String key, String secret, HttpClient httpClient) {
    return new DefaultScaniiClient(target, key, secret, httpClient);
  }

  // batch clients:
  public static ScaniiBatchClient createBatch(ScaniiTarget target, String key, String secret) {
    return new ScaniiBatchClient(new DefaultScaniiClient(target, key, secret, HttpClients.createDefault()));
  }

  public static ScaniiBatchClient createBatch(ScaniiTarget target,
                                              String key,
                                              String secret,
                                              int maxConcurrentRequests) {
    return new ScaniiBatchClient(new DefaultScaniiClient(target, key, secret, HttpClients.createDefault()), maxConcurrentRequests);
  }

  public static ScaniiBatchClient createBatch(ScaniiTarget target,
                                              String key,
                                              String secret,
                                              int maxConcurrentRequests, HttpClient httpClient) {
    return new ScaniiBatchClient(new DefaultScaniiClient(target, key, secret, httpClient), maxConcurrentRequests);
  }
}
