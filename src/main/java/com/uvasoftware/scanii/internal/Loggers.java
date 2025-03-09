package com.uvasoftware.scanii.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Loggers {
  private static final Logger LOG = LoggerFactory.getLogger(Loggers.class);

  public static Logger build() {
    Exception e = new Exception();
    StackTraceElement caller = e.getStackTrace()[1];
    LOG.trace("building LOGGER for {}", caller.getClassName());
    return LoggerFactory.getLogger(caller.getClassName());

  }
}
