### A pure Java interface to the Scanii content processing service - https://scanii.com


### How to use this client

#### Installing using Maven coordinates:

```
<dependency>
  <groupId>com.uvasoftware</groupId>
  <artifactId>scanii-java</artifactId>
  <version>${latest.release.version}</version>
</dependency>
```
#### Installing using gradle:

```
compile group: 'com.uvasoftware', name: 'scanii-java', version: '${latest.release.version}'
```

### Basic usage:
 
```
 // creating the client
 ScaniiClient client = new ScaniiClient(ScaniiTarget.v2_1, KEY, SECRET);
 
 // scans a file
 result = client.process(Paths.get("/tmp/foo.doc"));
 System.out.println(result.getFindings()); 
```

Please note that you will need a valid scanii.com account and API Credentials. 

More advanced usage examples can be found [here](https://github.com/uvasoftware/scanii-java/blob/master/src/test/java/com/scanii/client/ScaniiClientTest.java)

General documentation on scanii can be found [here](http://docs.scanii.com)

This library supports JDK 7 and above. 

