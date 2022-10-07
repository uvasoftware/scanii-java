package com.uvasoftware.scanii;

import com.uvasoftware.scanii.impl.DefaultScaniiClient;
import com.uvasoftware.scanii.models.ScaniiAuthToken;
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
    ScaniiAuthToken token = new ScaniiAuthToken();
    token.setResourceId("123");
    ScaniiClient client = ScaniiClients.createDefault(ScaniiTarget.AUTO, token);
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
