package com.scanii.client;

/**
 * Unchecked exception wrapper.
 */
public class ScaniiException extends RuntimeException {
  public ScaniiException(String message, Throwable cause) {
    super(message, cause);
  }

  public ScaniiException(String message) {
    super(message);
  }

  public ScaniiException(Throwable cause) {
    super(cause);
  }
}
