package com.uvasoftware.scanii.impl;

import com.uvasoftware.scanii.internal.JSON;

class ErrorExtractor {
  String extract(String contents) {
    var js =  JSON.load(contents);
    if (js.has("error")) {
      return js.get("error").asText();
    }
    return contents;
  }
}
