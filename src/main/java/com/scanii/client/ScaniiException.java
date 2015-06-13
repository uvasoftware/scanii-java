package com.scanii.client;

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
