package com.uvasoftware.scanii.impl;

import com.uvasoftware.scanii.internal.JSON;

class ErrorExtractor {
  String extract(String contents) {
    return JSON.load(contents).get("error").asText();
  }
}
