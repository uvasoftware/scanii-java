package com.scanii.client.batch;

import com.scanii.client.ScaniiClients;
import com.scanii.client.ScaniiTarget;
import com.scanii.client.misc.Systems;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

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
    int count = 100;

    final CountDownLatch latch = new CountDownLatch(count);

    IntStream.rangeClosed(1, 100).forEach(i -> {
      try {
        final Path tempFile = Systems.randomFile(1024);

        batchClient.submit(tempFile, (result) -> {
          System.out.println(String.format("File %s checksum: %s findings: %s", result.getResourceId(), result.getChecksum(), result.getFindings()));
          latch.countDown();
          try {
            Files.deleteIfExists(tempFile);
          } catch (IOException e) {
            Assertions.fail(e);
          }
          Assertions.assertEquals(0, result.getFindings().size());
        });

      } catch (Exception ex) {
        throw new IllegalStateException(ex);
      }
    });

    latch.await(60, TimeUnit.SECONDS);
  }
}
