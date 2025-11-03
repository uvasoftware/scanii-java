package com.uvasoftware.scanii.internal;

import com.uvasoftware.scanii.ScaniiClient;
import com.uvasoftware.scanii.ScaniiClients;
import com.uvasoftware.scanii.ScaniiException;
import com.uvasoftware.scanii.ScaniiTarget;
import com.uvasoftware.scanii.models.*;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Thread safe client to the Scanii content processing service.
 * Please note that this client does not throw checked exceptions; all exceptions are wrapped around a ScaniiException that extends RuntimeException
 *
 * @see <a href="https://uvasoftware.github.io/openapi/v22/">spec</a>
 */
public class DefaultScaniiClient implements ScaniiClient {
  private static final Logger LOG = Loggers.build();
  private final HttpClient httpClient;
  private final ScaniiTarget target;
  private final String authHeader;
  private final String userAgentHeader;
  private final ErrorExtractor errorExtractor;

  public DefaultScaniiClient(ScaniiTarget target, String key, String secret, HttpClient httpClient) {
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
    this.userAgentHeader = HttpHeaders.UA + "/v" + ScaniiClients.VERSION;
    this.errorExtractor = new ErrorExtractor();

    LOG.debug("creating client using key {} against target {}", key, target);

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

    HttpPost req = new HttpPost(target.resolve("/v2.2/files"));
    buildMultiPart(content, callback, metadata, req);

    return processResponse(req);
  }


  @Override
  public ScaniiProcessingResult process(InputStream content, String callback, Map<String, String> metadata) {
    Objects.requireNonNull(content, "content stream cannot be null");
    Objects.requireNonNull(metadata, "metadata cannot be null");

    HttpPost req = new HttpPost(target.resolve("/v2.2/files"));
    buildMultiPart(content, callback, metadata, req);

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

    HttpPost req = new HttpPost(target.resolve("/v2.2/files/async"));
    buildMultiPart(content, callback, metadata, req);

    return processAsyncResponse(req);
  }

  private ScaniiPendingResult processAsyncResponse(HttpPost req) {
    try {

      //noinspection Duplicates
      return httpClient.execute(req, response -> {
        String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getCode() != 202) {
          parseAndThrowError(response, responseEntity);
        }

        ScaniiPendingResult result = JSON.load(responseEntity, ScaniiPendingResult.class);
        extractRequestMetadata(result, response);
        result.setRawResponse(responseEntity);
        return result;

      });
    } catch (IOException e) {
      throw new ScaniiException(e);
    }
  }

  @Override
  public ScaniiPendingResult processAsync(InputStream content, String callback, Map<String, String> metadata) {
    Objects.requireNonNull(content, "content stream cannot be null");
    Objects.requireNonNull(metadata, "metadata cannot be null");

    HttpPost req = new HttpPost(target.resolve("/v2.2/files/async"));
    buildMultiPart(content, callback, metadata, req);

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

    HttpGet req = new HttpGet(target.resolve("/v2.2/files/" + id));
    addHeaders(req);

    try {
      return httpClient.execute(req, response -> {

        if (response.getCode() == 404) {
          return Optional.empty();
        }

        String responseEntity = EntityUtils.toString(response.getEntity());

        ScaniiProcessingResult result = JSON.load(responseEntity, ScaniiProcessingResult.class);

        extractRequestMetadata(result, response);
        result.setRawResponse(responseEntity);

        return Optional.of(result);

      });
    } catch (IOException e) {
      throw new ScaniiException(e);
    }
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

    HttpPost req = new HttpPost(target.resolve("/v2.2/files/fetch"));
    addHeaders(req);

    List<NameValuePair> fields = new ArrayList<>();

    if (callback != null) {
      fields.add(new BasicNameValuePair("callback", callback));
    }

    fields.add(new BasicNameValuePair("location", location));

    metadata.forEach((k, v) -> {
      fields.add(new BasicNameValuePair(String.format("metadata[%s]", k), v));
    });

    try {
      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(fields);
      req.setEntity(entity);

      //noinspection Duplicates
      return httpClient.execute(req, response -> {
        String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getCode() != 202) {
          parseAndThrowError(response, responseEntity);
        }

        ScaniiPendingResult result = JSON.load(responseEntity, ScaniiPendingResult.class);

        extractRequestMetadata(result, response);
        result.setRawResponse(responseEntity);

        return result;
      });
    } catch (IOException e) {
      throw new ScaniiException(e);
    }

  }

  @Override
  public boolean ping() {
    HttpGet req = new HttpGet(target.resolve("/v2.2/ping"));
    addHeaders(req);

    try {
      return httpClient.execute(req, response -> response.getCode() == 200);
    } catch (IOException e) {
      throw new ScaniiException(e);
    }
  }

  @Override
  public ScaniiAuthToken createAuthToken(int timeout, TimeUnit timeoutUnit) {

    if (!(timeout > 0)) {
      throw new IllegalArgumentException("timeout must be a positive number");
    }

    HttpPost req = new HttpPost(target.resolve("/v2.2/auth/tokens"));
    addHeaders(req);

    List<NameValuePair> fields = new ArrayList<>();

    fields.add(new BasicNameValuePair("timeout", String.valueOf(timeoutUnit.toSeconds(timeout))));

    try {
      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(fields);
      req.setEntity(entity);

      //noinspection Duplicates
      return httpClient.execute(req, response -> {
        String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getCode() != 201) {
          parseAndThrowError(response, responseEntity);
        }

        ScaniiAuthToken result = JSON.load(responseEntity, ScaniiAuthToken.class);

        extractRequestMetadata(result, response);
        result.setRawResponse(responseEntity);

        return result;
      });
    } catch (IOException e) {
      throw new ScaniiException(e);
    }

  }

  @Override
  public boolean deleteAuthToken(String id) {
    Objects.requireNonNull(id, "id cannot be null");

    HttpDelete req = new HttpDelete(target.resolve("/v2.2/auth/tokens/" + id));
    addHeaders(req);

    try {
      //noinspection Duplicates
      return httpClient.execute(req, response -> {
        if (response.getCode() != 204) {
          String responseEntity = EntityUtils.toString(response.getEntity());
          parseAndThrowError(response, responseEntity);
        }
        return true;
      });
    } catch (IOException e) {
      throw new ScaniiException(e);
    }
  }

  @Override
  public ScaniiAuthToken retrieveAuthToken(String id) {
    Objects.requireNonNull(id, "id cannot be null");

    HttpGet req = new HttpGet(target.resolve("/v2.2/auth/tokens/" + id));
    addHeaders(req);

    try {
      return httpClient.execute(req, response -> {
        String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getCode() != 200) {
          parseAndThrowError(response, responseEntity);
        }

        ScaniiAuthToken result = JSON.load(responseEntity, ScaniiAuthToken.class);

        extractRequestMetadata(result, response);
        result.setRawResponse(responseEntity);

        return result;
      });
    } catch (IOException e) {
      throw new ScaniiException(e);
    }

  }

  @Override
  public ScaniiAccountInfo retrieveAccountInfo() {
    HttpGet req = new HttpGet(target.resolve("/v2.2/account.json"));
    addHeaders(req);

    try {
      return httpClient.execute(req, response -> {
        String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getCode() != 200) {
          parseAndThrowError(response, responseEntity);
        }

        ScaniiAccountInfo result = JSON.load(responseEntity, ScaniiAccountInfo.class);

        extractRequestMetadata(result, response);
        result.setRawResponse(responseEntity);

        return result;
      });
    } catch (IOException e) {
      throw new ScaniiException(e);
    }
  }

  private void addHeaders(HttpUriRequest request) {
    request.addHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, authHeader));
    request.addHeader(new BasicHeader(HttpHeaders.USER_AGENT, userAgentHeader));
  }

  private void extractRequestMetadata(ScaniiResult result, HttpResponse response) {

    result.setStatusCode(response.getCode());

    if (response.containsHeader(HttpHeaders.X_REQUEST_HEADER)) {
      result.setRequestId(response.getLastHeader(HttpHeaders.X_REQUEST_HEADER).getValue());
    }

    if (response.containsHeader(HttpHeaders.X_HOST_HEADER)) {
      result.setHostId(response.getLastHeader(HttpHeaders.X_HOST_HEADER).getValue());

    }

    if (response.containsHeader(HttpHeaders.LOCATION)) {
      result.setResourceLocation(response.getLastHeader(HttpHeaders.LOCATION).getValue());
    }
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  private void parseAndThrowError(HttpResponse response, String responseEntity) {
    if (response.getFirstHeader(HttpHeaders.CONTENT_TYPE) != null && response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue().equals("application/json")) {
      throw new ScaniiException(response.getCode(), errorExtractor.extract(responseEntity));
    } else {
      throw new ScaniiException(response.getCode(), responseEntity);
    }

  }

  private ScaniiProcessingResult processResponse(HttpPost req) {
    try {
      return httpClient.execute(req, response -> {

        String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getCode() != 201) {
          parseAndThrowError(response, responseEntity);
        }

        ScaniiProcessingResult result = JSON.load(responseEntity, ScaniiProcessingResult.class);
        extractRequestMetadata(result, response);
        result.setRawResponse(responseEntity);

        return result;

      });
    } catch (IOException e) {
      throw new ScaniiException(e);
    }
  }

  private void buildMultiPart(Path content, String callback, Map<String, String> metadata, HttpPost req) {
    addHeaders(req);

    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
      .addBinaryBody("file", content.toFile());

    metadata.forEach((k, v) -> {
      multipartEntityBuilder.addTextBody(String.format("metadata[%s]", k), v);
    });

    if (callback != null) {
      multipartEntityBuilder.addTextBody("callback", callback, ContentType.TEXT_PLAIN);
    }


    req.setEntity(multipartEntityBuilder.build());
  }

  private void buildMultiPart(InputStream content, String callback, Map<String, String> metadata, HttpPost req) {
    addHeaders(req);

    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
      .addBinaryBody("file", content);

    metadata.forEach((k, v) -> {
      multipartEntityBuilder.addTextBody(String.format("metadata[%s]", k), v);
    });

    if (callback != null) {
      multipartEntityBuilder.addTextBody("callback", callback, ContentType.TEXT_PLAIN);
    }


    req.setEntity(multipartEntityBuilder.build());
  }
}
