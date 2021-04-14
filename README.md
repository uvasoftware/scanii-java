### A pure Java interface to the Scanii content processing service - https://scanii.com

### How to use this client

#### Installing using Maven coordinates:

```xml

<dependency>
  <groupId>com.uvasoftware</groupId>
  <artifactId>scanii-java</artifactId>
  <version>${latest.release.version}</version>
</dependency>
```

### Sample usage:

```java
package com.uvasoftware.scanii;

import com.uvasoftware.scanii.models.ScaniiProcessingResult;

import java.nio.file.Paths;

public class Sample {
  public static void main(String[] args) {
    // in this example args contains the key secret and file path:
    String key = args[0];
    String secret = args[1];
    ScaniiClient client = ScaniiClients.createDefault(key, secret);
    ScaniiProcessingResult result = client.process(Paths.get(args[2]));
    System.out.printf("checksum: %s, content-type: %s and findings: %s%n",
      result.getChecksum(),
      result.getContentType(),
      result.getFindings());
    if (result.getFindings().isEmpty()) {
      System.out.println("Content is safe!");
    }
  }
}
```

Please note that you will need a valid scanii.com account and API Credentials.

* More advanced usage examples can be
  found [here](https://github.com/uvasoftware/scanii-java/blob/master/src/test/java/com/uvasoftware/scanii/ScaniiClientTest.java)
* General documentation on scanii can be found [here](http://docs.scanii.com)
* Javadocs can be found [here](https://www.javadoc.io/doc/com.uvasoftware/scanii-java/latest/index.html)
