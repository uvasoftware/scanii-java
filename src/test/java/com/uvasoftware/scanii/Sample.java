package com.uvasoftware.scanii;

import com.uvasoftware.scanii.models.ScaniiProcessingResult;

import java.nio.file.Paths;

public class Sample {
  public static void main(String[] args) {
    ScaniiClient client = ScaniiClients.createDefault(ScaniiTarget.latest(), args[0], args[1]);
    ScaniiProcessingResult result = client.process(Paths.get(args[2]));
    System.out.println(String.format("checksum: %s, content-type: %s and findings: %s",
      result.getChecksum(),
      result.getContentType(),
      result.getFindings())
    );
  }
}
