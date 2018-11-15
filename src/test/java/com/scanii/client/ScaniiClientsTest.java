package com.scanii.client;

import com.scanii.client.batch.ScaniiBatchClient;
import com.scanii.client.impl.DefaultScaniiClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScaniiClientsTest {

  @Test
  void createDefault() {
    ScaniiClient client = ScaniiClients.createDefault(ScaniiTarget.latest(), "key", "secret");
    Assertions.assertNotNull(client);
  }

  @Test
  void createDefault1() {
    ScaniiClient client = ScaniiClients.createDefault(ScaniiTarget.latest(), "key");
    Assertions.assertNotNull(client);
  }

  @Test
  void createDefault2() {
    CloseableHttpClient hc = HttpClients.createMinimal();
    ScaniiClient client = ScaniiClients.createDefault(ScaniiTarget.latest(), "key", "secret", hc);
    Assertions.assertNotNull(client);
    Assertions.assertEquals(hc, ((DefaultScaniiClient) client).getHttpClient());
  }

  @Test
  void createBatch() {
    ScaniiBatchClient client = ScaniiClients.createBatch(ScaniiTarget.latest(), "key", "secret");
    Assertions.assertNotNull(client);
  }

  @Test
  void createBatch1() {
    ScaniiBatchClient client = ScaniiClients.createBatch(ScaniiTarget.latest(), "key", "secret", 10);
    Assertions.assertNotNull(client);
  }

  @Test
  void createBatch2() {
    CloseableHttpClient hc = HttpClients.createMinimal();
    ScaniiBatchClient client = ScaniiClients.createBatch(ScaniiTarget.latest(), "key", "secret", 32, hc);
    Assertions.assertNotNull(client);
    Assertions.assertEquals(hc, ((DefaultScaniiClient) client.getClient()).getHttpClient());

  }
}
