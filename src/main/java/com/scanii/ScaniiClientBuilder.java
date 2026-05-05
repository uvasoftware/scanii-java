package com.scanii;

import com.scanii.internal.DefaultScaniiClient;
import com.scanii.models.ScaniiAuthToken;

import java.net.http.HttpClient;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builder for constructing a {@link ScaniiClient} with custom configuration.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ScaniiClient client = ScaniiClients.builder()
 *   .target(ScaniiTarget.US1)
 *   .credentials("key", "secret")
 *   .userAgent("my-app/1.0")
 *   .httpClient(customClient)
 *   .build();
 * }</pre>
 */
public class ScaniiClientBuilder {
  @SuppressWarnings("deprecation")
  private ScaniiTarget target = ScaniiTarget.AUTO;
  private String key;
  private String secret;
  private HttpClient httpClient;
  private String userAgent;
  private final Map<String, String> headers = new LinkedHashMap<>();

  ScaniiClientBuilder() {
  }

  /**
   * Sets the target region.
   *
   * <p>Use an explicit regional constant ({@link ScaniiTarget#US1}, {@link ScaniiTarget#EU1},
   * etc.) for production. If not set, the client defaults to {@link ScaniiTarget#AUTO}, which is
   * deprecated — a runtime warning will be emitted.</p>
   *
   * @param target the target region {@link ScaniiTarget}.
   * @return this builder.
   */
  public ScaniiClientBuilder target(ScaniiTarget target) {
    this.target = target;
    return this;
  }

  /**
   * Sets the API key and secret for authentication.
   *
   * @param key    an API key.
   * @param secret an API secret.
   * @return this builder.
   */
  public ScaniiClientBuilder credentials(String key, String secret) {
    this.key = key;
    this.secret = secret;
    return this;
  }

  /**
   * Sets authentication using a temporary auth token.
   *
   * @param authToken the auth token to use.
   * @return this builder.
   */
  public ScaniiClientBuilder authToken(ScaniiAuthToken authToken) {
    this.key = authToken.getResourceId();
    this.secret = "";
    return this;
  }

  /**
   * Sets a custom {@link HttpClient} instance.
   *
   * @param httpClient the HTTP client to use.
   * @return this builder.
   */
  public ScaniiClientBuilder httpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
    return this;
  }

  /**
   * Sets a custom user agent string. This is prepended to the default SDK user agent.
   *
   * @param userAgent a user agent string identifying your application (e.g. "my-app/1.0").
   * @return this builder.
   */
  public ScaniiClientBuilder userAgent(String userAgent) {
    this.userAgent = userAgent;
    return this;
  }

  /**
   * Adds a custom HTTP header to be included in every request.
   *
   * @param name  the header name.
   * @param value the header value.
   * @return this builder.
   */
  public ScaniiClientBuilder header(String name, String value) {
    this.headers.put(name, value);
    return this;
  }

  /**
   * Builds the {@link ScaniiClient} with the configured settings.
   *
   * @return the new scanii client.
   * @throws IllegalStateException if credentials have not been set.
   */
  @SuppressWarnings("deprecation")
  public ScaniiClient build() {
    if (key == null) {
      throw new IllegalStateException("credentials or authToken must be set");
    }
    if (target == ScaniiTarget.AUTO) {
      System.err.println("[scanii] DEPRECATION: No explicit target set; defaulting to ScaniiTarget.AUTO " +
        "(https://api.scanii.com). This does not guarantee regional data placement. " +
        "Use ScaniiTarget.US1 (or another regional constant) for explicit data residency control. " +
        "ScaniiTarget.AUTO will be removed in a future major version.");
    }
    HttpClient client = httpClient != null ? httpClient : HttpClient.newHttpClient();
    return new DefaultScaniiClient(target, key, secret, client, userAgent, Collections.unmodifiableMap(new LinkedHashMap<>(headers)));
  }
}
