### A pure Java interface to the Scanii content processing service - https://scanii.com


### How to use this client

#### Installing using Maven coordinates:

```
<dependency>
  <groupId>com.uvasoftware</groupId>
  <artifactId>scanii-java</artifactId>
  <version>2.8</version>
</dependency>
```
#### Installing using gradle:

```
compile group: 'com.uvasoftware', name: 'scanii-java', version: '2.8'
```

### Basic usage:
 
```
// creating the client
 ScaniiClient client = new ScaniiClient(ScaniiTarget.v2_0, KEY, SECRET);
 
 // scans a file
 result = client.process(Paths.get("/tmp/foo.doc"));
 System.out.println(result.getFindings()); 
```

Please note that you will need a valid scanii.com account and API Credentials. 

More advanced usage examples can be found [here](https://github.com/uvasoftware/scanii-java/blob/master/src/test/com/scanii/client/ScaniiClientTest.java)

General documentation on scanii can be found [here](http://docs.scanii.com)

