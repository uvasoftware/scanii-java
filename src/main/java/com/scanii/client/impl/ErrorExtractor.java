package com.scanii.client.impl;

import com.scanii.client.ScaniiTarget;
import com.scanii.client.internal.JSON;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class ErrorExtractor {
  private final Map<ScaniiTarget, Function<String, String>> extractors = new HashMap<>();

  ErrorExtractor() {

    Function<String, String> extractv21 = (response) -> JSON.load(response).get("error").asText();
    Function<String, String> extractv20 = (response) -> JSON.load(response).get("message").asText();

    extractors.put(ScaniiTarget.v2_1, extractv21);
    extractors.put(ScaniiTarget.v2_1_AP1, extractv21);
    extractors.put(ScaniiTarget.v2_1_US1, extractv21);
    extractors.put(ScaniiTarget.v2_1_EU1, extractv21);


    extractors.put(ScaniiTarget.v2_0, extractv20);
    extractors.put(ScaniiTarget.v2_0_AP1, extractv20);
    extractors.put(ScaniiTarget.v2_0_US1, extractv20);
    extractors.put(ScaniiTarget.v2_0_EU1, extractv20);

  }

  String extract(ScaniiTarget target, String contents) {
    return extractors.get(target).apply(contents);
  }
}
