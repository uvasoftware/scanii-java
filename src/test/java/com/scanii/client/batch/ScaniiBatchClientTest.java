package com.scanii.client.batch;

import com.scanii.client.ScaniiClients;
import com.scanii.client.ScaniiTarget;
import com.scanii.client.misc.Systems;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

class ScaniiBatchClientTest {
  private static final String KEY;
  private static final String SECRET;

  static {
    KEY = System.getenv("SCANII_CREDS").split(":")[0];
    SECRET = System.getenv("SCANII_CREDS").split(":")[1];
  }

  @Test
  void testSubmit() throws Exception {
    ScaniiBatchClient batchClient = ScaniiClients.createBatch(ScaniiTarget.latest(), KEY, SECRET);
    final AtomicInteger results = new AtomicInteger();

    int count = 100;
    for (int i = 0; i < count; i++) {
      batchClient.submit(Systems.randomFile(1024), (result) -> {
        System.out.println(String.format("File %s checksum: %s findings: %s", result.getResourceId(), result.getChecksum(), result.getFindings()));
        results.incrementAndGet();
      });
    }

    while (results.get() != count) {
      Thread.sleep(1000);
    }
  }
}
