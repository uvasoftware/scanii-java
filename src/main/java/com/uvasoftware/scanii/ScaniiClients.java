package com.uvasoftware.scanii;

import com.uvasoftware.scanii.internal.DefaultScaniiClient;
import com.uvasoftware.scanii.models.ScaniiAuthToken;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

/**
 * Static helper class to speed up the instantiation of new clients.
 */
public class ScaniiClients {
  public static final String VERSION;

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
  public static ScaniiClient createDefault(ScaniiTarget target, ScaniiAuthToken authToken) {
    return new DefaultScaniiClient(target, authToken.getResourceId(), "", HttpClients.createDefault());
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
   * Creates a default client using an API key/secret pair and routing to the nearest processing endpoint
   *
   * @param key    an API key to be used.
   * @param secret an API secret to be used.
   * @return the new scanii client.
   */
  public static ScaniiClient createDefault(String key, String secret) {
    return new DefaultScaniiClient(ScaniiTarget.AUTO, key, secret, HttpClients.createDefault());
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
   * Creates a default client using an API key/secret pair and a custom Apache HTTP client.
   *
   * @param target     the target region {@link ScaniiTarget}.
   * @param authToken  the auth token to used for authentication.
   * @param httpClient a Apache HTTP Client you would like to use.
   * @return the new scanii client.
   */
  public static ScaniiClient createDefault(ScaniiTarget target, ScaniiAuthToken authToken, HttpClient httpClient) {
    return new DefaultScaniiClient(target, authToken.getResourceId(), "", httpClient);
  }
}
