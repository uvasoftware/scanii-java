package com.uvasoftware.scanii.impl;

import com.uvasoftware.scanii.ScaniiTarget;
import com.uvasoftware.scanii.internal.JSON;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class ErrorExtractor {
  private final Map<ScaniiTarget, Function<String, String>> extractors = new HashMap<>();

  ErrorExtractor() {

    Function<String, String> extractV21 = (response) -> JSON.load(response).get("error").asText();
    Function<String, String> extractV20 = (response) -> JSON.load(response).get("message").asText();

    extractors.put(ScaniiTarget.v2_1, extractV21);
    extractors.put(ScaniiTarget.v2_1_AP1, extractV21);
    extractors.put(ScaniiTarget.v2_1_US1, extractV21);
    extractors.put(ScaniiTarget.v2_1_EU1, extractV21);


    extractors.put(ScaniiTarget.v2_0, extractV20);
    extractors.put(ScaniiTarget.v2_0_AP1, extractV20);
    extractors.put(ScaniiTarget.v2_0_US1, extractV20);
    extractors.put(ScaniiTarget.v2_0_EU1, extractV20);

  }

  String extract(ScaniiTarget target, String contents) {
    return extractors.get(target).apply(contents);
  }
}
