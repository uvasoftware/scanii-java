package com.uvasoftware.scanii;

import com.uvasoftware.scanii.impl.DefaultScaniiClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ScaniiClientsTest {

  @Test
  void createDefault() {
    ScaniiClient client = ScaniiClients.createDefault(ScaniiTarget.AUTO, "key", "secret");
    Assertions.assertNotNull(client);
  }

  @Test
  void createDefault1() {
    ScaniiClient client = ScaniiClients.createDefault(ScaniiTarget.AUTO, "key");
    Assertions.assertNotNull(client);
  }

  @Test
  void createDefault2() {
    CloseableHttpClient hc = HttpClients.createMinimal();
    ScaniiClient client = ScaniiClients.createDefault(ScaniiTarget.AUTO, "key", "secret", hc);
    Assertions.assertNotNull(client);
    Assertions.assertEquals(hc, ((DefaultScaniiClient) client).getHttpClient());
  }
}
