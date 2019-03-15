package com.uvasoftware.scanii;

import com.uvasoftware.scanii.models.ScaniiAccountInfo;
import com.uvasoftware.scanii.models.ScaniiAuthToken;
import com.uvasoftware.scanii.models.ScaniiPendingResult;
import com.uvasoftware.scanii.models.ScaniiProcessingResult;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface ScaniiClient {

  /**
   * Submits a file to be processed @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content  path to the file to be processed
   * @param metadata optional metadata to be added to this file
   * @return scanii result @see ScaniiProcessingResult
   */
  ScaniiProcessingResult process(Path content, Map<String, String> metadata);
  
  /**
   * Submits a file stream to be processed @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content  stream of the file to be processed
   * @param metadata optional metadata to be added to this file
   * @return scanii result @see ScaniiProcessingResult
   */  
  ScaniiProcessingResult process(InputStream content, Map<String, String> metadata);  

  /**
   * Submits a file to be processed @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a> with optional callback.
   *
   * @param content  path to the file to be processed
   * @param callback location (URL) to be notified and receive the result
   * @param metadata optional metadata to be added to this file
   * @return scanii result @see ScaniiProcessingResult
   */
  ScaniiProcessingResult process(Path content, String callback, Map<String, String> metadata);
  
  
  /**
   * Submits a file stream to be processed @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a> with optional callback.
   *
   * @param content  file content to be processed
   * @param callback location (URL) to be notified and receive the result
   * @param metadata optional metadata to be added to this file
   * @return scanii result @see ScaniiProcessingResult
   */  
  ScaniiProcessingResult process(InputStream content, String callback, Map<String, String> metadata);  

  /**
   * Submits a file to be processed @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content path to the file to be processed
   * @return scanii result @see ScaniiProcessingResult
   */
  ScaniiProcessingResult process(Path content);
  
  /**
   * Submits a file stream to be processed @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content stream of the file to be processed
   * @return scanii result @see ScaniiProcessingResult
   */  
  ScaniiProcessingResult process(InputStream content);  

  /**
   * Submits a file to be processed asynchronously @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content  path to the file to be processed
   * @param metadata optional metadata to be added to this file
   * @return processing result @see ScaniiPendingResult
   */
  ScaniiPendingResult processAsync(Path content, Map<String, String> metadata);

  /**
   * Submits a file stream to be processed asynchronously @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content  stream of the file to be processed
   * @param metadata optional metadata to be added to this file
   * @return processing result @see ScaniiPendingResult
   */
  ScaniiPendingResult processAsync(InputStream content, Map<String, String> metadata);  

  /**
   * Submits a file to be processed asynchronously @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content  path to the file to be processed
   * @param callback location (URL) to be notified and receive the result
   * @param metadata optional metadata to be added to this file
   * @return processing result @see ScaniiPendingResult
   */
  ScaniiPendingResult processAsync(Path content, String callback, Map<String, String> metadata);
  
  /**
   * Submits a file stream to be processed @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a> with optional callback.
   *
   * @param content  file content to be processed
   * @param callback location (URL) to be notified and receive the result
   * @param metadata optional metadata to be added to this file
   * @return scanii result @see ScaniiProcessingResult
   */  
  ScaniiPendingResult processAsync(InputStream content, String callback, Map<String, String> metadata);  

  /**
   * Submits a file to be processed asynchronously @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content path to the file to be processed
   * @return processing result @see ScaniiPendingResult
   */
  ScaniiPendingResult processAsync(Path content);
  
  /**
   * Submits a file content stream to be processed asynchronously @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param content stream of the file to be processed
   * @return processing result @see ScaniiPendingResult
   */  
  ScaniiPendingResult processAsync(InputStream content);

  /**
   * Fetches the results of a previously processed file @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param id id of the content/file to be retrieved
   * @return optional  @see ScaniiProcessingResult
   */
  Optional<ScaniiProcessingResult> retrieve(String id);

  /**
   * Makes a fetch call to scanii @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param location location (URL) of the content to be processed
   * @return scanii result @see ScaniiResult
   */
  ScaniiPendingResult fetch(String location);

  /**
   * Makes a fetch call to scanii @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param location location (URL) of the content to be processed
   * @param callback location (URL) to be notified and receive the result
   * @return scanii result @see ScaniiResult
   */
  ScaniiPendingResult fetch(String location, String callback);

  /**
   * Makes a fetch call to scanii @see <a href="http://docs.scanii.com/v2.1/resources.html#files">http://docs.scanii.com/v2.1/resources.html#files</a>
   *
   * @param location location (URL) of the content to be processed
   * @param callback location (URL) to be notified and receive the result
   * @param metadata optional metadata to be added to this file
   * @return scanii result @see ScaniiResult
   */
  ScaniiPendingResult fetch(String location, String callback, Map<String, String> metadata);

  /**
   * Pings the scanii service using the credentials provided @see <a href="http://docs.scanii.com/v2.1/resources.html#ping">http://docs.scanii.com/v2.1/resources.html#ping</a>
   *
   * @return true if we saw a pong back from scanii
   */
  boolean ping();

  /**
   * Creates a new temporary authentication token @see <a href="http://docs.scanii.com/v2.1/resources.html#auth-tokens">http://docs.scanii.com/v2.1/resources.html#auth-tokens</a>
   *
   * @param timeout     how long the token should be valid for"
   * @param timeoutUnit unit use to calculate the timeout
   * @return the new auth token
   */
  ScaniiAuthToken createAuthToken(int timeout, TimeUnit timeoutUnit);

  /**
   * Deletes a pre existing auth token
   *
   * @param id the id of the token to be deleted
   */
  void deleteAuthToken(String id);

  /**
   * Retrieves a previously created auth token
   *
   * @param id the id of the token to be retrieved
   * @return scanii result @see ScaniiAuthToken
   */
  ScaniiAuthToken retrieveAuthToken(String id);

  /**
   * Retrieves account information @see <a href="https://docs.scanii.com/v2.1/resources.html#account">https://docs.scanii.com/v2.1/resources.html#account</a>
   *
   * @return your account information
   */
  ScaniiAccountInfo retrieveAccountInfo();
}
