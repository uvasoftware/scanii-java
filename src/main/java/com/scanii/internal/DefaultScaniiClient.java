package com.scanii.internal;

import com.scanii.ScaniiClient;
import com.scanii.ScaniiClients;
import com.scanii.ScaniiException;
import com.scanii.ScaniiTarget;
import com.scanii.models.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Thread safe client to the Scanii content processing service.
 * Please note that this client does not throw checked exceptions; all exceptions are wrapped around a ScaniiException that extends RuntimeException
 *
 * @see <a href="https://scanii.github.io/openapi/v22/">spec</a>
 */
public class DefaultScaniiClient implements ScaniiClient {
  private static final System.Logger LOG = System.getLogger(DefaultScaniiClient.class.getName());
  private final HttpClient httpClient;
  private final ScaniiTarget target;
  private final String authHeader;
  private final String userAgentHeader;
  private final Map<String, String> customHeaders;

  public DefaultScaniiClient(ScaniiTarget target, String key, String secret, HttpClient httpClient, String userAgent, Map<String, String> customHeaders) {
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException("key must not be null or empty");
    }

    if (key.contains(":")) {
      throw new IllegalArgumentException("key must not contain a \":\"");
    }

    if (secret == null) {
      throw new IllegalArgumentException("secret must not be null or empty");
    }

    this.target = target;
    this.httpClient = httpClient;
    this.authHeader = "Basic " + Base64.getEncoder().encodeToString((key + ":" + secret).getBytes(StandardCharsets.UTF_8));

    String defaultUA = HttpHeaders.UA + "/v" + ScaniiClients.VERSION;
    this.userAgentHeader = (userAgent != null && !userAgent.isEmpty())
      ? userAgent + " " + defaultUA
      : defaultUA;

    this.customHeaders = customHeaders;

    LOG.log(System.Logger.Level.DEBUG, "creating client using key {0} against target {1}", key, target);

  }

  @Override
  public ScaniiProcessingResult process(Path content, Map<String, String> metadata) {
    return process(content, null, metadata);
  }

  @Override
  public ScaniiProcessingResult process(InputStream content, Map<String, String> metadata) {
    return process(content, null, metadata);
  }

  @Override
  public ScaniiProcessingResult process(Path content, String callback, Map<String, String> metadata) {
    Objects.requireNonNull(content, "content path cannot be null");
    Objects.requireNonNull(metadata, "metadata cannot be null");

    if (!Files.exists(content)) {
      throw new IllegalArgumentException("content path does not exist");
    }

    MultipartBodyPublisher multipart = buildMultiPart(content, callback, metadata);
    HttpRequest req = buildPost(target.resolve("/v2.2/files"), multipart);

    return processResponse(req);
  }


  @Override
  public ScaniiProcessingResult process(InputStream content, String callback, Map<String, String> metadata) {
    Objects.requireNonNull(content, "content stream cannot be null");
    Objects.requireNonNull(metadata, "metadata cannot be null");

    MultipartBodyPublisher multipart = buildMultiPart(content, callback, metadata);
    HttpRequest req = buildPost(target.resolve("/v2.2/files"), multipart);

    return processResponse(req);
  }


  @Override
  public ScaniiProcessingResult process(Path content) {
    return process(content, Collections.emptyMap());
  }

  @Override
  public ScaniiProcessingResult process(InputStream content) {
    return process(content, Collections.emptyMap());
  }

  @Override
  public ScaniiPendingResult processAsync(Path content, Map<String, String> metadata) {
    return processAsync(content, null, metadata);
  }

  @Override
  public ScaniiPendingResult processAsync(InputStream content, Map<String, String> metadata) {
    return processAsync(content, null, metadata);
  }

  @Override
  public ScaniiPendingResult processAsync(Path content, String callback, Map<String, String> metadata) {
    Objects.requireNonNull(content, "content path cannot be null");
    Objects.requireNonNull(metadata, "metadata cannot be null");

    if (!Files.exists(content)) {
      throw new IllegalArgumentException("content path does not exist");
    }

    MultipartBodyPublisher multipart = buildMultiPart(content, callback, metadata);
    HttpRequest req = buildPost(target.resolve("/v2.2/files/async"), multipart);

    return processAsyncResponse(req);
  }

  private ScaniiPendingResult processAsyncResponse(HttpRequest req) {
    HttpResponse<String> response = send(req);

    if (response.statusCode() != 202) {
      parseAndThrowError(response);
    }

    ScaniiPendingResult result = JSON.load(response.body(), ScaniiPendingResult.class);
    extractRequestMetadata(result, response);
    result.setRawResponse(response.body());
    return result;
  }

  @Override
  public ScaniiPendingResult processAsync(InputStream content, String callback, Map<String, String> metadata) {
    Objects.requireNonNull(content, "content stream cannot be null");
    Objects.requireNonNull(metadata, "metadata cannot be null");

    MultipartBodyPublisher multipart = buildMultiPart(content, callback, metadata);
    HttpRequest req = buildPost(target.resolve("/v2.2/files/async"), multipart);

    return processAsyncResponse(req);
  }


  @Override
  public ScaniiPendingResult processAsync(Path content) {
    return processAsync(content, Collections.emptyMap());
  }

  @Override
  public ScaniiPendingResult processAsync(InputStream content) {
    return processAsync(content, Collections.emptyMap());
  }

  @Override
  public Optional<ScaniiProcessingResult> retrieve(String id) {
    Objects.requireNonNull(id, "resource id cannot be null");

    HttpRequest req = buildGet(target.resolve("/v2.2/files/" + id));
    HttpResponse<String> response = send(req);

    if (response.statusCode() == 404) {
      return Optional.empty();
    }

    ScaniiProcessingResult result = JSON.load(response.body(), ScaniiProcessingResult.class);
    extractRequestMetadata(result, response);
    result.setRawResponse(response.body());

    return Optional.of(result);
  }

  @Override
  public ScaniiPendingResult fetch(String location) {
    return fetch(location, null, Collections.emptyMap());
  }

  @Override
  public ScaniiPendingResult fetch(String location, String callback) {
    return fetch(location, callback, Collections.emptyMap());
  }

  @Override
  public ScaniiPendingResult fetch(String location, String callback, Map<String, String> metadata) {
    Objects.requireNonNull(location, "content location cannot be null");
    Objects.requireNonNull(metadata, "metadata cannot be null");

    List<String> fields = new ArrayList<>();

    if (callback != null) {
      fields.add(encode("callback") + "=" + encode(callback));
    }

    fields.add(encode("location") + "=" + encode(location));

    metadata.forEach((k, v) -> fields.add(encode(String.format("metadata[%s]", k)) + "=" + encode(v)));

    String formBody = String.join("&", fields);

    HttpRequest req = newRequestBuilder(target.resolve("/v2.2/files/fetch"))
      .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
      .POST(HttpRequest.BodyPublishers.ofString(formBody))
      .build();

    HttpResponse<String> response = send(req);

    if (response.statusCode() != 202) {
      parseAndThrowError(response);
    }

    ScaniiPendingResult result = JSON.load(response.body(), ScaniiPendingResult.class);
    extractRequestMetadata(result, response);
    result.setRawResponse(response.body());

    return result;
  }

  @Override
  public boolean ping() {
    HttpRequest req = buildGet(target.resolve("/v2.2/ping"));
    HttpResponse<String> response = send(req);
    return response.statusCode() == 200;
  }

  @Override
  public ScaniiAuthToken createAuthToken(int timeout, TimeUnit timeoutUnit) {

    if (!(timeout > 0)) {
      throw new IllegalArgumentException("timeout must be a positive number");
    }

    List<String> fields = new ArrayList<>();
    fields.add(encode("timeout") + "=" + encode(String.valueOf(timeoutUnit.toSeconds(timeout))));

    String formBody = String.join("&", fields);

    HttpRequest req = newRequestBuilder(target.resolve("/v2.2/auth/tokens"))
      .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
      .POST(HttpRequest.BodyPublishers.ofString(formBody))
      .build();

    HttpResponse<String> response = send(req);

    if (response.statusCode() != 201 && response.statusCode() != 200) {
      parseAndThrowError(response);
    }

    ScaniiAuthToken result = JSON.load(response.body(), ScaniiAuthToken.class);
    extractRequestMetadata(result, response);
    result.setRawResponse(response.body());

    return result;
  }

  @Override
  public boolean deleteAuthToken(String id) {
    Objects.requireNonNull(id, "id cannot be null");

    HttpRequest req = newRequestBuilder(target.resolve("/v2.2/auth/tokens/" + id))
      .DELETE()
      .build();

    HttpResponse<String> response = send(req);

    if (response.statusCode() != 204) {
      parseAndThrowError(response);
    }
    return true;
  }

  @Override
  public ScaniiAuthToken retrieveAuthToken(String id) {
    Objects.requireNonNull(id, "id cannot be null");

    HttpRequest req = buildGet(target.resolve("/v2.2/auth/tokens/" + id));
    HttpResponse<String> response = send(req);

    if (response.statusCode() != 200) {
      parseAndThrowError(response);
    }

    ScaniiAuthToken result = JSON.load(response.body(), ScaniiAuthToken.class);
    extractRequestMetadata(result, response);
    result.setRawResponse(response.body());

    return result;
  }

  @Override
  public ScaniiAccountInfo retrieveAccountInfo() {
    HttpRequest req = buildGet(target.resolve("/v2.2/account.json"));
    HttpResponse<String> response = send(req);

    if (response.statusCode() != 200) {
      parseAndThrowError(response);
    }

    ScaniiAccountInfo result = JSON.load(response.body(), ScaniiAccountInfo.class);
    extractRequestMetadata(result, response);
    result.setRawResponse(response.body());

    return result;
  }

  private HttpRequest.Builder newRequestBuilder(String uri) {
    HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(uri))
      .header(HttpHeaders.AUTHORIZATION, authHeader)
      .header(HttpHeaders.USER_AGENT, userAgentHeader);
    customHeaders.forEach(builder::header);
    return builder;
  }

  private HttpRequest buildGet(String uri) {
    return newRequestBuilder(uri).GET().build();
  }

  private HttpRequest buildPost(String uri, MultipartBodyPublisher multipart) {
    return newRequestBuilder(uri)
      .header(HttpHeaders.CONTENT_TYPE, multipart.contentType())
      .POST(multipart.build())
      .build();
  }

  private HttpResponse<String> send(HttpRequest request) {
    try {
      return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      throw new ScaniiException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ScaniiException(e);
    }
  }

  private void extractRequestMetadata(ScaniiResult result, HttpResponse<String> response) {
    result.setStatusCode(response.statusCode());

    response.headers().firstValue(HttpHeaders.X_REQUEST_HEADER).ifPresent(result::setRequestId);
    response.headers().firstValue(HttpHeaders.X_HOST_HEADER).ifPresent(result::setHostId);
    response.headers().firstValue(HttpHeaders.LOCATION).ifPresent(result::setResourceLocation);
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  private void parseAndThrowError(HttpResponse<String> response) {
    String contentType = response.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse("");
    if (contentType.equals("application/json")) {
      Map<String, Object> js = JSON.load(response.body());
      String error = js.containsKey("error") ? js.get("error").toString() : response.body();
      throw new ScaniiException(response.statusCode(), error);
    } else {
      throw new ScaniiException(response.statusCode(), response.body());
    }
  }

  private ScaniiProcessingResult processResponse(HttpRequest req) {
    HttpResponse<String> response = send(req);

    if (response.statusCode() != 201) {
      parseAndThrowError(response);
    }

    ScaniiProcessingResult result = JSON.load(response.body(), ScaniiProcessingResult.class);
    extractRequestMetadata(result, response);
    result.setRawResponse(response.body());

    return result;
  }

  private MultipartBodyPublisher buildMultiPart(Path content, String callback, Map<String, String> metadata) {
    MultipartBodyPublisher multipart = new MultipartBodyPublisher()
      .addBinaryBody("file", content);

    metadata.forEach((k, v) -> multipart.addTextBody(String.format("metadata[%s]", k), v));

    if (callback != null) {
      multipart.addTextBody("callback", callback);
    }

    return multipart;
  }

  private MultipartBodyPublisher buildMultiPart(InputStream content, String callback, Map<String, String> metadata) {
    MultipartBodyPublisher multipart = new MultipartBodyPublisher()
      .addBinaryBody("file", content);

    metadata.forEach((k, v) -> multipart.addTextBody(String.format("metadata[%s]", k), v));

    if (callback != null) {
      multipart.addTextBody("callback", callback);
    }

    return multipart;
  }

  private static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
