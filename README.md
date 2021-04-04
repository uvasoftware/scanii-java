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

Javadocs: https://www.javadoc.io/doc/com.uvasoftware/scanii-java/latest/index.html

### Sample usage:
 
```java
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
```

Please note that you will need a valid scanii.com account and API Credentials. 

More advanced usage examples can be found [here](https://github.com/uvasoftware/scanii-java/blob/master/src/test/java/com/uvasoftware/scanii/ScaniiClientTest.java)

General documentation on scanii can be found [here](http://docs.scanii.com)
 

