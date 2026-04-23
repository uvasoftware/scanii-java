package com.scanii;

import com.scanii.models.ScaniiProcessingResult;

import java.nio.file.Path;

public class Sample {
  public static void main(String[] args) {
    // in this example args contains the key secret and file path:
    String key = args[0];
    String secret = args[1];
    ScaniiClient client = ScaniiClients.createDefault(key, secret);
    ScaniiProcessingResult result = client.process(Path.of(args[2]));
    System.out.printf("checksum: %s, content-type: %s and findings: %s%n",
      result.getChecksum(),
      result.getContentType(),
      result.getFindings());
    if (result.getFindings().isEmpty()) {
      System.out.println("Content is safe!");
    }
  }
}
