package com.scanii.client.batch;

import com.scanii.client.ScaniiClient;
import com.scanii.client.ScaniiResult;
import com.scanii.client.ScaniiTarget;
import com.scanii.client.misc.Systems;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class ScaniiBatchClientTest {
  private static final String KEY = System.getenv("TEST_KEY");
  private static final String SECRET = System.getenv("TEST_SECRET");

  @Test
  public void testSubmit() throws Exception {
    ScaniiBatchClient bclient = new ScaniiBatchClient(new ScaniiClient(ScaniiTarget.latest(), KEY, SECRET));
    final AtomicInteger results = new AtomicInteger();

    int count = 100;
    for (int i = 0; i < count; i++) {
      bclient.submit(Systems.randomFile(1024), new ScaniiResultHandler() {
        @Override
        public void handle(ScaniiResult result) {
          System.out.println(String.format("File %s checksum: %s findings: %s", result.getResourceId(), result.getChecksum(), result.getFindings()));
          results.incrementAndGet();
        }
      });
    }

    while (results.get() != count) {
      Thread.sleep(1000);
    }
  }
}
