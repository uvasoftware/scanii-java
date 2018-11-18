package com.uvasoftware.scanii.impl;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.uvasoftware.scanii.ScaniiClient;
import com.uvasoftware.scanii.ScaniiClients;
import com.uvasoftware.scanii.ScaniiException;
import com.uvasoftware.scanii.ScaniiTarget;
import com.uvasoftware.scanii.internal.Endpoints;
import com.uvasoftware.scanii.internal.HttpHeaders;
import com.uvasoftware.scanii.internal.JSON;
import com.uvasoftware.scanii.internal.Loggers;
import com.uvasoftware.scanii.models.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Thread safe client to the Scanii content processing service.
 * Please note that this client does not throw checked exceptions, all exceptions are wrapped around a ScaniiException that extends RuntimeException
 *
 * @see <a href="http://docs.scanii.com/v2.1/resources.html">http://docs.scanii.com/v2.1/resources.html</a>
 */
public class DefaultScaniiClient implements ScaniiClient {
  private static final Logger LOG = Loggers.build();
  private final HttpClient httpClient;
  private final ScaniiTarget target;
  private final String authHeader;
  private final String userAgentHeader;
  private final ErrorExtractor errorExtractor;


  public DefaultScaniiClient(ScaniiTarget target, String key, String secret, HttpClient httpClient) {
    Preconditions.checkNotNull(key, "please pass a non-null key");
    Preconditions.checkNotNull(secret, "please pass a non-null secret");

    this.target = target;
    this.httpClient = httpClient;
    this.authHeader = "Basic " + Base64.getEncoder().encodeToString((key + ":" + secret).getBytes(Charsets.UTF_8));
    this.userAgentHeader = HttpHeaders.UA + "/v" + ScaniiClients.VERSION;
    this.errorExtractor = new ErrorExtractor();

    LOG.debug("creating client using key {} against target {}", key, target);

  }

  @Override
  public ScaniiProcessingResult process(Path content, Map<String, String> metadata) {
    Preconditions.checkNotNull(content, "content path cannot be null");
    Preconditions.checkNotNull(metadata, "metadata cannot be null");
    Preconditions.checkArgument(Files.exists(content), "content path does not exist");

    HttpPost req = new HttpPost(Endpoints.resolve(target, "files"));
    addHeaders(req);

    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
      .addBinaryBody("file", content.toFile());

    metadata.forEach((k, v) -> {
      multipartEntityBuilder.addTextBody(String.format("metadata[%s]", k), v);
    });

    req.setEntity(multipartEntityBuilder.build());

    try {
      return httpClient.execute(req, response -> {

        String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getStatusLine().getStatusCode() != 201) {
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

  @Override
  public ScaniiProcessingResult process(Path content) {
    return process(content, ImmutableMap.of());
  }

  @Override
  public ScaniiPendingResult processAsync(Path content, Map<String, String> metadata) {
    Preconditions.checkNotNull(content, "content path cannot be null");
    Preconditions.checkNotNull(metadata, "metadata cannot be null");
    Preconditions.checkArgument(Files.exists(content), "content path does not exist");

    HttpPost req = new HttpPost(Endpoints.resolve(target, "files/async"));
    addHeaders(req);

    MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
      .addBinaryBody("file", content.toFile());

    metadata.forEach((k, v) -> {
      multipartEntityBuilder.addTextBody(String.format("metadata[%s]", k), v);
    });

    req.setEntity(multipartEntityBuilder.build());

    try {

      //noinspection Duplicates
      return httpClient.execute(req, response -> {
        String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getStatusLine().getStatusCode() != 202) {
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
  public ScaniiPendingResult processAsync(Path content) {
    return processAsync(content, ImmutableMap.of());
  }

  @Override
  public Optional<ScaniiProcessingResult> retrieve(String id) {
    Preconditions.checkNotNull(id, "resource id cannot be null");

    HttpGet req = new HttpGet(Endpoints.resolve(target, "files/" + id));
    addHeaders(req);

    try {
      return httpClient.execute(req, response -> {

        if (response.getStatusLine().getStatusCode() == 404) {
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
    return fetch(location, null, ImmutableMap.of());
  }

  @Override
  public ScaniiPendingResult fetch(String location, String callback) {
    return fetch(location, callback, ImmutableMap.of());
  }

  @Override
  public ScaniiPendingResult fetch(String location, String callback, Map<String, String> metadata) {
    Preconditions.checkNotNull(location, "content location cannot be null");
    Preconditions.checkNotNull(metadata, "metadata cannot be null");

    HttpPost req = new HttpPost(Endpoints.resolve(target, "files/fetch"));
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

        if (response.getStatusLine().getStatusCode() != 202) {
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
    HttpGet req = new HttpGet(Endpoints.resolve(target, "ping"));
    addHeaders(req);

    try {
      return httpClient.execute(req, response -> response.getStatusLine().getStatusCode() == 200);
    } catch (IOException e) {
      throw new ScaniiException(e);
    }
  }

  @Override
  public ScaniiAuthToken createAuthToken(int timeout, TimeUnit timeoutUnit) {
    Preconditions.checkArgument(timeout > 0, "timeout must be a positive number");

    HttpPost req = new HttpPost(Endpoints.resolve(target, "auth/tokens"));
    addHeaders(req);

    List<NameValuePair> fields = new ArrayList<>();

    fields.add(new BasicNameValuePair("timeout", String.valueOf(timeoutUnit.toSeconds(timeout))));

    try {
      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(fields);
      req.setEntity(entity);

      //noinspection Duplicates
      return httpClient.execute(req, response -> {
        String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getStatusLine().getStatusCode() != 201) {
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
  public void deleteAuthToken(String id) {
    Preconditions.checkNotNull(id, "id cannot be null");

    HttpDelete req = new HttpDelete(Endpoints.resolve(target, "auth/tokens/" + id));
    addHeaders(req);

    try {
      //noinspection Duplicates
      httpClient.execute(req, response -> {
        if (response.getStatusLine().getStatusCode() != 204) {
          String responseEntity = EntityUtils.toString(response.getEntity());
          parseAndThrowError(response, responseEntity);
        }
        return null;
      });
    } catch (IOException e) {
      throw new ScaniiException(e);
    }
  }

  @Override
  public ScaniiAuthToken retrieveAuthToken(String id) {
    Preconditions.checkNotNull(id, "id cannot be null");

    HttpGet req = new HttpGet(Endpoints.resolve(target, "auth/tokens/" + id));
    addHeaders(req);

    try {
      return httpClient.execute(req, response -> {
        String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getStatusLine().getStatusCode() != 200) {
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
    HttpGet req = new HttpGet(Endpoints.resolve(target, "account.json"));
    addHeaders(req);

    try {
      return httpClient.execute(req, response -> {
        String responseEntity = EntityUtils.toString(response.getEntity());

        if (response.getStatusLine().getStatusCode() != 200) {
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
    request.addHeader(new BasicHeader(com.google.common.net.HttpHeaders.AUTHORIZATION, authHeader));
    request.addHeader(new BasicHeader(com.google.common.net.HttpHeaders.USER_AGENT, userAgentHeader));
  }

  private void extractRequestMetadata(ScaniiResult result, HttpResponse response) {

    result.setStatusCode(response.getStatusLine().getStatusCode());

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
    if (response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue().equals("application/json")) {
      throw new ScaniiException(response.getStatusLine().getStatusCode(), errorExtractor.extract(target, responseEntity));
    } else {
      throw new ScaniiException(response.getStatusLine().getStatusCode(), responseEntity);
    }
  }
}
