package com.scanii;

import com.scanii.models.ScaniiAccountInfo;
import com.scanii.models.ScaniiAuthToken;
import com.scanii.models.ScaniiPendingResult;
import com.scanii.models.ScaniiProcessingResult;
import com.scanii.models.ScaniiTraceResult;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface ScaniiClient {

  /**
   * Submits a file to be processed @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a>
   *
   * @param content  path to the file to be processed
   * @param metadata optional metadata to be added to this file
   * @return scanii result {@link ScaniiProcessingResult}
   */
  ScaniiProcessingResult process(Path content, Map<String, String> metadata);

  /**
   * Submits a file stream to be processed @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a>
   *
   * @param content  stream of the file to be processed
   * @param metadata optional metadata to be added to this file
   * @return scanii result {@link ScaniiProcessingResult}
   */
  ScaniiProcessingResult process(InputStream content, Map<String, String> metadata);

  /**
   * Submits a file to be processed @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a> with optional callback.
   *
   * @param content  path to the file to be processed
   * @param callback location (URL) to be notified and receive the result
   * @param metadata optional metadata to be added to this file
   * @return scanii result {@link ScaniiProcessingResult}
   */
  ScaniiProcessingResult process(Path content, String callback, Map<String, String> metadata);


  /**
   * Submits a file stream to be processed @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a> with optional callback.
   *
   * @param content  file content to be processed
   * @param callback location (URL) to be notified and receive the result
   * @param metadata optional metadata to be added to this file
   * @return scanii result {@link ScaniiProcessingResult}
   */
  ScaniiProcessingResult process(InputStream content, String callback, Map<String, String> metadata);

  /**
   * Submits a file to be processed @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a>
   *
   * @param content path to the file to be processed
   * @return scanii result {@link ScaniiProcessingResult}
   */
  ScaniiProcessingResult process(Path content);

  /**
   * Submits a file stream to be processed @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a>
   *
   * @param content stream of the file to be processed
   * @return scanii result {@link ScaniiProcessingResult}
   */
  ScaniiProcessingResult process(InputStream content);

  /**
   * Submits a file to be processed asynchronously @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a>
   *
   * @param content  path to the file to be processed
   * @param metadata optional metadata to be added to this file
   * @return processing result {@link ScaniiPendingResult}
   */
  ScaniiPendingResult processAsync(Path content, Map<String, String> metadata);

  /**
   * Submits a file stream to be processed asynchronously @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a>
   *
   * @param content  stream of the file to be processed
   * @param metadata optional metadata to be added to this file
   * @return processing result {@link ScaniiPendingResult}
   */
  ScaniiPendingResult processAsync(InputStream content, Map<String, String> metadata);

  /**
   * Submits a file to be processed asynchronously @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a>
   *
   * @param content  path to the file to be processed
   * @param callback location (URL) to be notified and receive the result
   * @param metadata optional metadata to be added to this file
   * @return processing result {@link ScaniiPendingResult}
   */
  ScaniiPendingResult processAsync(Path content, String callback, Map<String, String> metadata);

  /**
   * Submits a file stream to be processed @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a> with optional callback.
   *
   * @param content  file content to be processed
   * @param callback location (URL) to be notified and receive the result
   * @param metadata optional metadata to be added to this file
   * @return scanii result {@link ScaniiProcessingResult}
   */
  ScaniiPendingResult processAsync(InputStream content, String callback, Map<String, String> metadata);

  /**
   * Submits a file to be processed asynchronously @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a>
   *
   * @param content path to the file to be processed
   * @return processing result {@link ScaniiPendingResult}
   */
  ScaniiPendingResult processAsync(Path content);

  /**
   * Submits a file content stream to be processed asynchronously @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a>
   *
   * @param content stream of the file to be processed
   * @return processing result {@link ScaniiPendingResult}
   */
  ScaniiPendingResult processAsync(InputStream content);

  /**
   * Fetches the results of a previously processed file @see <a href="https://scanii.github.io/openapi/v22/">spec</a>
   *
   * @param id id of the content/file to be retrieved
   * @return optional  {@link ScaniiProcessingResult}
   */
  Optional<ScaniiProcessingResult> retrieve(String id);

  /**
   * Retrieves the processing trace for a previously processed file.
   * Returns an ordered list of events describing each stage of the processing pipeline.
   *
   * <p><strong>Preview:</strong> the trace endpoint is marked preview in the v2.2 spec —
   * the API surface may shift before it is marked stable.
   *
   * @param id id of the previously processed content
   * @return optional {@link ScaniiTraceResult}, empty if the id is not found
   * @see <a href="https://scanii.github.io/openapi/v22/">spec</a>
   */
  Optional<ScaniiTraceResult> retrieveTrace(String id);

  /**
   * Submits a remote URL for synchronous processing. The Scanii service fetches the content
   * at the given URL and scans it, returning the result immediately.
   *
   * @param location URI of the remote content to process
   * @param metadata optional metadata to be attached to this result
   * @return scanii result {@link ScaniiProcessingResult}
   * @see <a href="https://scanii.github.io/openapi/v22/">spec</a>
   */
  ScaniiProcessingResult processFromUrl(URI location, Map<String, String> metadata);

  /**
   * Submits a remote URL for synchronous processing. The Scanii service fetches the content
   * at the given URL and scans it, returning the result immediately.
   *
   * @param location URI of the remote content to process
   * @return scanii result {@link ScaniiProcessingResult}
   * @see <a href="https://scanii.github.io/openapi/v22/">spec</a>
   */
  ScaniiProcessingResult processFromUrl(URI location);

  /**
   * Makes a fetch call to scanii @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a>
   *
   * @param location location (URL) of the content to be processed
   * @return scanii result {@link ScaniiPendingResult}
   */
  ScaniiPendingResult fetch(String location);

  /**
   * Makes a fetch call to scanii @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a>
   *
   * @param location location (URL) of the content to be processed
   * @param callback location (URL) to be notified and receive the result
   * @return scanii result {@link ScaniiPendingResult}
   */
  ScaniiPendingResult fetch(String location, String callback);

  /**
   * Makes a fetch call to scanii @see <a href="https://docs.scanii.com/v2.2/resources.html#files">https://docs.scanii.com/v2.2/resources.html#files</a>
   *
   * @param location location (URL) of the content to be processed
   * @param callback location (URL) to be notified and receive the result
   * @param metadata optional metadata to be added to this file
   * @return scanii result {@link ScaniiPendingResult}
   */
  ScaniiPendingResult fetch(String location, String callback, Map<String, String> metadata);

  /**
   * Pings the scanii service using the credentials provided @see <a href="https://docs.scanii.com/v2.2/resources.html#ping">https://docs.scanii.com/v2.2/resources.html#ping</a>
   *
   * @return true if we saw a pong back from scanii, false otherwise
   */
  boolean ping();

  /**
   * Creates a new temporary authentication token @see <a href="https://docs.scanii.com/v2.2/resources.html#auth-tokens">https://docs.scanii.com/v2.2/resources.html#auth-tokens</a>
   *
   * @param timeout     how long the token should be valid for"
   * @param timeoutUnit unit used to calculate the timeout
   * @return the new auth token
   */
  ScaniiAuthToken createAuthToken(int timeout, TimeUnit timeoutUnit);

  /**
   * Deletes a pre-existing auth token
   *
   * @param id the id of the token to be deleted
   */
  boolean deleteAuthToken(String id);

  /**
   * Retrieves a previously created auth token
   *
   * @param id the id of the token to be retrieved
   * @return scanii result {@link ScaniiAuthToken}
   */
  ScaniiAuthToken retrieveAuthToken(String id);

  /**
   * Retrieves account information @see <a href="https://docs.scanii.com/v2.2/resources.html#account">https://docs.scanii.com/v2.2/resources.html#account</a>
   *
   * @return your account information
   */
  ScaniiAccountInfo retrieveAccountInfo();
}
