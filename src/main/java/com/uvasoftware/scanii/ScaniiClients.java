package com.uvasoftware.scanii;

import com.uvasoftware.scanii.batch.ScaniiBatchClient;
import com.uvasoftware.scanii.impl.DefaultScaniiClient;
import com.uvasoftware.scanii.internal.Loggers;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

/**
 * Static helper class to speed up the instantiation of new clients.
 */
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

  /**
   * Creates a default client using an authentication token.
   *
   * @param target    the target region {@link ScaniiTarget}.
   * @param authToken the auth token to used for authentication.
   * @return the new scanii client.
   */
  public static ScaniiClient createDefault(ScaniiTarget target, String authToken) {
    return new DefaultScaniiClient(target, authToken, "", HttpClients.createDefault());
  }

  /**
   * Creates a default client using an API key/secret pair.
   *
   * @param target the target region {@link ScaniiTarget}.
   * @param key    an API key to be used.
   * @param secret an API secret to be used.
   * @return the new scanii client.
   */
  public static ScaniiClient createDefault(ScaniiTarget target, String key, String secret) {
    return new DefaultScaniiClient(target, key, secret, HttpClients.createDefault());
  }

  /**
   * Creates a default client using an API key/secret pair and a custom Apache HTTP client.
   *
   * @param target     the target region {@link ScaniiTarget}.
   * @param key        a API key to be used.
   * @param secret     a API secret to be used.
   * @param httpClient a Apache HTTP Client you would like to use.
   * @return the new scanii client.
   */
  public static ScaniiClient createDefault(ScaniiTarget target, String key, String secret, HttpClient httpClient) {
    return new DefaultScaniiClient(target, key, secret, httpClient);
  }

  /**
   * Creates a new scanii batch client using an API key/secret pair.
   *
   * @param target the target region {@link ScaniiTarget}.
   * @param key    a API key to be used.
   * @param secret a API secret to be used.
   * @return the new scanii client.
   */
  // batch clients:
  public static ScaniiBatchClient createBatch(ScaniiTarget target, String key, String secret) {
    return new ScaniiBatchClient(new DefaultScaniiClient(target, key, secret, HttpClients.createDefault()));
  }

  /**
   * Creates a new scanii batch client using an API key/secret pair.
   *
   * @param target                the target region {@link ScaniiTarget}.
   * @param key                   a API key to be used.
   * @param secret                a API secret to be used.
   * @param maxConcurrentRequests maximum number of concurrent API requests.
   * @return the new scanii client.
   */
  public static ScaniiBatchClient createBatch(ScaniiTarget target,
                                              String key,
                                              String secret,
                                              int maxConcurrentRequests) {
    return new ScaniiBatchClient(new DefaultScaniiClient(target, key, secret, HttpClients.createDefault()), maxConcurrentRequests);
  }

  /**
   * Creates a new scanii batch client using an API key/secret pair and a custom Apache HTTP client.
   *
   * @param target                the target region {@link ScaniiTarget}.
   * @param key                   a API key to be used.
   * @param secret                a API secret to be used.
   * @param maxConcurrentRequests maximum number of concurrent API requests.
   * @param httpClient            a Apache HTTP Client you would like to use.
   * @return the new scanii client.
   */
  public static ScaniiBatchClient createBatch(ScaniiTarget target,
                                              String key,
                                              String secret,
                                              int maxConcurrentRequests, HttpClient httpClient) {
    return new ScaniiBatchClient(new DefaultScaniiClient(target, key, secret, httpClient), maxConcurrentRequests);
  }
}
