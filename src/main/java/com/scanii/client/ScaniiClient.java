package com.scanii.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.scanii.client.misc.Endpoints;
import com.scanii.client.misc.HttpHeaders;
import com.scanii.client.misc.JSON;
import com.scanii.client.misc.Loggers;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Thread safe client to the Scanii content processing service.
 * Please note that this client does not throw checked exceptions, all exceptions are wrapped around a ScaniiException that extends RuntimeException
 *
 * @see <a href="http://docs.scanii.com/v2.1/resources.html">http://docs.scanii.com/v2.1/resources.html</a>
 */
public class ScaniiClient {
  private static final Logger LOG = Loggers.build();

  private final ScaniiTarget target;
  private final String key;
  private final String secret;
  private final int DEFAULT_CONNECTION_TIMEOUT = 30000;
  private final int DEFAULT_READ_TIMEOUT = 60000;

  /**
   * Creates a new Scanii Client
   *
   * @param target the API version and location target @see ScaniiTarget
   * @param key    your API key
   * @param secret your API secret
   */
  public ScaniiClient(ScaniiTarget target, String key, String secret) {
    this.target = target;
    this.key = key;

    // it's better to use null then an empty secret:
    if (secret != null && secret.length() == 0) {
      secret = null;
    }
    this.secret = secret;
  }

  /**
   * Submits a file to be processed @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content  path to the file to be processed
   * @param metadata optional metadata to be added to this file
   * @return scanii result @see ScaniiResult
   */
  public ScaniiResult process(Path content, Map<String, String> metadata) {
    Preconditions.checkNotNull(content);
    Preconditions.checkNotNull(metadata);

    try {
      HttpRequest r = HttpRequest.post(Endpoints.resolve(target, "files"))
        .userAgent(HttpHeaders.UA)
        .basic(key, secret)
        .part("file", content.toFile())
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
        .readTimeout(DEFAULT_READ_TIMEOUT);

      for (Map.Entry<String, String> e : metadata.entrySet()) {
        r.part(metadataKey(e.getKey()), e.getValue());
      }

      if (r.code() != 201) {
        throw new ScaniiException(String.format("Invalid HTTP response from service, code: %s message: %s", r.code(), r.body()));
      }

      return processResponse(r);

    } catch (Exception ex) {
      throw new ScaniiException(ex);
    }
  }

  /**
   * Submits a file to be processed @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content path to the file to be processed
   * @return scanii result @see ScaniiResult
   */
  public ScaniiResult process(Path content) {
    return process(content, ImmutableMap.<String, String>of());
  }

  /**
   * Submits a file to be processed @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content  path to the file to be processed
   * @param metadata optional metadata to be added to this file
   * @return scanii result @see ScaniiResult
   */
  public ScaniiResult processAsync(Path content, Map<String, String> metadata) {
    Preconditions.checkNotNull(content);
    Preconditions.checkNotNull(metadata);

    try {
      HttpRequest r = HttpRequest.post(Endpoints.resolve(target, "files/async"))
        .userAgent(HttpHeaders.UA)
        .basic(key, secret)
        .part("file", content.toFile())
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
        .readTimeout(DEFAULT_READ_TIMEOUT);

      for (Map.Entry<String, String> e : metadata.entrySet()) {
        r.part(metadataKey(e.getKey()), e.getValue());
      }

      if (r.code() != 202) {
        throw new ScaniiException(String.format("Invalid HTTP response from service, code: %s message: %s", r.code(), r.body()));
      }

      return processResponse(r);

    } catch (Exception ex) {
      throw new ScaniiException(ex);
    }
  }

  /**
   * Submits a file to be processed @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content path to the file to be processed
   * @return scanii result @see ScaniiResult
   */
  public ScaniiResult processAsync(Path content) {
    return processAsync(content, ImmutableMap.<String, String>of());
  }

  /**
   * Fetches the results of a previously processed file @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param id id of the content/file to be retrieved
   * @return scanii result @see ScaniiResult
   */
  public ScaniiResult retrieve(String id) {
    try {
      HttpRequest r = HttpRequest.get(Endpoints.resolve(target, "files/" + id))
        .userAgent(HttpHeaders.UA)
        .basic(key, secret)
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
        .readTimeout(DEFAULT_READ_TIMEOUT);

      if (r.code() != 200) {
        throw new ScaniiException(String.format("Invalid HTTP response from service, code: %s message: %s", r.code(), r.body()));
      }

      return processResponse(r);

    } catch (Exception ex) {
      throw new ScaniiException(ex);
    }
  }

  /**
   * Makes a fetch call to scanii @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param location location (URL) of the content to be processed
   * @return scanii result @see ScaniiResult
   */
  public ScaniiResult fetch(String location) {
    return fetch(location, null, ImmutableMap.<String, String>of());
  }

  /**
   * Makes a fetch call to scanii @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param location location (URL) of the content to be processed
   * @param callback location (URL) to be notified and receive the result
   * @return scanii result @see ScaniiResult
   */
  public ScaniiResult fetch(String location, String callback) {
    return fetch(location, callback, ImmutableMap.<String, String>of());
  }

  /**
   * Makes a fetch call to scanii @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param location location (URL) of the content to be processed
   * @param callback location (URL) to be notified and receive the result
   * @param metadata optional metadata to be added to this file
   * @return scanii result @see ScaniiResult
   */
  public ScaniiResult fetch(String location, String callback, Map<String, String> metadata) {
    Preconditions.checkNotNull(location);
    Preconditions.checkNotNull(metadata);

    try {

      HttpRequest r = HttpRequest.post(Endpoints.resolve(target, "files/fetch"))
        .userAgent(HttpHeaders.UA)
        .basic(key, secret)
        .form("location", location)
        .form("callback", callback)
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
        .readTimeout(DEFAULT_READ_TIMEOUT);

      for (Map.Entry<String, String> e : metadata.entrySet()) {
        r.form(metadataKey(e.getKey()), e.getValue());
      }

      if (r.code() != 202) {
        throw new ScaniiException(String.format("Invalid HTTP response from service, code: %s message: %s", r.code(), r.body()));
      }

      return processResponse(r);

    } catch (Exception ex) {
      throw new ScaniiException(ex);
    }
  }

  /**
   * Pings the scanii service using the credentials provided @see <a href="http://docs.scanii.com/v2.1/resources.html#ping">http://docs.scanii.com/v2.1/resources.html#ping</a>
   *
   * @return true if we saw a pong back from scanii
   */
  public boolean ping() {
    try {

      HttpRequest r = HttpRequest.get(Endpoints.resolve(target, "ping"))
        .userAgent(HttpHeaders.UA)
        .basic(key, secret)
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
        .readTimeout(DEFAULT_READ_TIMEOUT);

      if (r.code() != 200) {
        throw new ScaniiException(String.format("Invalid HTTP response from service, code: %s message: %s", r.code(), r.body()));
      }

      return true;

    } catch (Exception ex) {
      throw new ScaniiException(ex);
    }
  }


  /**
   * Creates a new temporary authentication token @see <a href="http://docs.scanii.com/v2.1/resources.html#auth-tokens">http://docs.scanii.com/v2.1/resources.html#auth-tokens</a>
   *
   * @param timeout     how long the token should be valid for"
   * @param timeoutUnit unit use to calculate the timeout
   * @return scanii result @see ScaniiResult
   */
  public ScaniiResult createAuthToken(int timeout, TimeUnit timeoutUnit) {
    HttpRequest r = HttpRequest.post(Endpoints.resolve(target, "auth/tokens"))
      .userAgent(HttpHeaders.UA)
      .basic(key, secret)
      .form("timeout", timeoutUnit.toSeconds(timeout))
      .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
      .readTimeout(DEFAULT_READ_TIMEOUT);

    if (r.code() != 201) {
      throw new ScaniiException(String.format("Invalid HTTP response from service, code: %s message: %s", r.code(), r.body()));
    }

    return processResponse(r);

  }

  /**
   * Deletes a previously created authentication token
   *
   * @param id the id of the token to be deleted
   * @return true if the deletion succeed
   */
  public boolean deleteAuthToken(String id) {
    HttpRequest r = HttpRequest.delete(Endpoints.resolve(target, "auth/tokens" + "/" + id))
      .userAgent(HttpHeaders.UA)
      .basic(key, secret)
      .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
      .readTimeout(DEFAULT_READ_TIMEOUT);

    if (r.code() != 204) {
      throw new ScaniiException(String.format("Invalid HTTP response from service, code: %s message: %s", r.code(), r.body()));
    }

    return true;

  }

  /**
   * Retrieves a previously created auth token
   *
   * @param id the id of the token to be retrieved
   * @return scanii result @see ScaniiResult
   */
  public ScaniiResult retrieveAuthToken(String id) {
    HttpRequest r = HttpRequest.get(Endpoints.resolve(target, "auth/tokens" + "/" + id))
      .userAgent(HttpHeaders.UA)
      .basic(key, secret)
      .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
      .readTimeout(DEFAULT_READ_TIMEOUT);

    if (r.code() != 200) {
      throw new ScaniiException(String.format("Invalid HTTP response from service, code: %s message: %s", r.code(), r.body()));
    }

    return processResponse(r);

  }

  private ScaniiResult processResponse(HttpRequest response) {
    try {
      ScaniiResult result = new ScaniiResult();

      result.setResourceLocation(response.header(HttpHeaders.LOCATION));
      result.setRequestId(response.header(HttpHeaders.X_REQUEST_HEADER));
      result.setHostId(response.header(HttpHeaders.X_HOST_HEADER));

      JsonNode js = JSON.load(response.body());
      result.setRawResponse(js.toString());
      result.setResourceId(js.get("id").asText());

      if (js.has("findings")) {
        result.setContentType(js.get("content_type").asText());
        result.setContentLength(js.get("content_length").asLong());
        result.setChecksum(js.get("checksum").asText());

        List<String> findings = new ArrayList<String>();
        Iterator<JsonNode> iter = js.get("findings").elements();
        while (iter.hasNext()) {
          findings.add(iter.next().asText());
        }
        result.setFindings(Collections.unmodifiableList(findings));

      }

      if (js.has("message")) {
        result.setMessage(js.get("message").asText());
      }

      if (js.has("expiration_date")) {
        result.setExpirationDate(js.get("expiration_date").asText());
      }
      if (js.has("creation_date")) {
        result.setCreationDate(js.get("creation_date").asText());
      }
      if (js.has("metadata")) {
        Iterator<Map.Entry<String, JsonNode>> iter = js.get("metadata").fields();
        while (iter.hasNext()) {
          Map.Entry<String, JsonNode> entry = iter.next();

          LOG.debug("processing metadata k: {} v: {}", entry.getKey(), entry.getValue().asText());
          result.getMetadata().put(entry.getKey(), entry.getValue().asText());
        }

      }

      return result;

    } catch (Exception ex) {
      throw new ScaniiException("Invalid response from service " + ex.getMessage(), ex);
    }
  }

  private String metadataKey(String key) {
    return String.format("metadata[%s]", key);
  }

}
