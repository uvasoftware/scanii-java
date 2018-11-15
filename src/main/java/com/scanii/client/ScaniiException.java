package com.scanii.client;

/**
 * Unchecked exception wrapper.
 */
@SuppressWarnings("WeakerAccess")
public class ScaniiException extends RuntimeException {
  public ScaniiException(String message, Throwable cause) {
    super(message, cause);
  }

  public ScaniiException(Throwable cause) {
    super(cause);
  }

  public ScaniiException(int code, String error) {
    super(String.format("Error: %s with HTTP code: %d", error, code));
  }
}
