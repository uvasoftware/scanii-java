package com.uvasoftware.scanii.internal;

class ErrorExtractor {
  String extract(String contents) {
    var js =  JSON.load(contents);
    if (js.has("error")) {
      return js.get("error").asText();
    }
    return contents;
  }
}
