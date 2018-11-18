package com.uvasoftware.scanii;

/**
 * Unchecked exception wrapper.
 */
@SuppressWarnings("WeakerAccess")
public class ScaniiException extends RuntimeException {
  public ScaniiException(Throwable cause) {
    super(cause);
  }

  public ScaniiException(int code, String error) {
    super(String.format("Error: [%s] with HTTP code: %d", error, code));
  }
}
