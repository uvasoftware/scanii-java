package com.scanii.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.kevinsawicki.http.HttpRequest;
import com.scanii.client.misc.HttpHeaders;
import com.scanii.client.misc.JSON;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Thred safe client to the Scanii content processing service
 */
public class ScaniiClient {

  private final ScaniiTarget target;
  private final String key;
  private final String secret;
  private final int DEFAULT_CONNECTION_TIMEOUT = 30000;
  private final int DEFAULT_READ_TIMEOUT = 60000;


  public ScaniiClient(ScaniiTarget target, String key, String secret) {
    this.target = target;
    this.key = key;
    this.secret = secret;
  }

  public ScaniiResult process(Path content) {
    try {
      HttpRequest r = HttpRequest.post(Endpoints.resolve(target, "files"))
        .basic(key, secret)
        .part("file", content.toFile())
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
        .readTimeout(DEFAULT_READ_TIMEOUT);

      if (r.code() != 201) {
        throw new ScaniiException(String.format("Invalid HTTP response from service, code: %s message: %s", r.code(), r.body()));
      }

      ScaniiResult result = processResponse(r);
      return result;

    } catch (Exception ex) {
      throw new ScaniiException(ex);
    }
  }

  public ScaniiResult processAsync(Path content) {
    try {
      HttpRequest r = HttpRequest.post(Endpoints.resolve(target, "files/async"))
        .basic(key, secret)
        .part("file", content.toFile())
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
        .readTimeout(DEFAULT_READ_TIMEOUT);

      if (r.code() != 202) {
        throw new ScaniiException(String.format("Invalid HTTP response from service, code: %s message: %s", r.code(), r.body()));
      }

      ScaniiResult result = processResponse(r);

      return result;

    } catch (Exception ex) {
      throw new ScaniiException(ex);
    }
  }

  public ScaniiResult result(String id) {
    try {
      HttpRequest r = HttpRequest.get(Endpoints.resolve(target, "files/" + id))
        .basic(key, secret)
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
        .readTimeout(DEFAULT_READ_TIMEOUT);

      if (r.code() != 200) {
        throw new ScaniiException(String.format("Invalid HTTP response from service, code: %s message: %s", r.code(), r.message()));
      }

      ScaniiResult result = processResponse(r);
      return result;

    } catch (Exception ex) {
      throw new ScaniiException(ex);
    }
  }

  public ScaniiResult fetch(String location) {
    return fetch(location, null);
  }

  public ScaniiResult fetch(String location, String callback) {
    try {

      HttpRequest r = HttpRequest.post(Endpoints.resolve(target, "files/fetch"))
        .basic(key, secret)
        .form("location", location)
        .form("callback", callback)
        .connectTimeout(DEFAULT_CONNECTION_TIMEOUT)
        .readTimeout(DEFAULT_READ_TIMEOUT);

      if (r.code() != 202) {
        throw new ScaniiException(String.format("Invalid HTTP response from service, code: %s message: %s", r.code(), r.message()));
      }

      ScaniiResult result = processResponse(r);
      return result;

    } catch (Exception ex) {
      throw new ScaniiException(ex);
    }
  }


  private ScaniiResult processResponse(HttpRequest response) {
    try {
      ScaniiResult result = new ScaniiResult();

      result.setResourceLocation(response.header(HttpHeaders.LOCATION));
      result.setRequestId(response.header(HttpHeaders.X_REQUEST_HEADER));
      result.setHostId(response.header(HttpHeaders.X_HOST_HEADER));

      JsonNode js = JSON.load(response.body());
      result.setRawResponse(js.toString());
      result.setFileId(js.get("id").asText());

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

      return result;

    } catch (Exception ex) {
      throw new ScaniiException("Invalid response from service " + ex.getMessage(), ex);
    }
  }

}
