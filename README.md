# scanii-java

Official Java SDK for the [Scanii](https://www.scanii.com) content processing API.

## SDK Principles

1. **Light.** SDKs keep a small and narrow list of dependencies so they don't add ops burden to customers. Ideally zero runtime dependencies — stdlib only. Test dependencies are also kept minimal. Build tooling (compilers, bundlers) doesn't count.

2. **Up to date.** SDKs are always current with the latest Scanii API spec. When the API evolves, the SDKs are upgraded promptly rather than carrying stale versions for years.

3. **Integration-only.** The SDK's job is to wrap the Scanii REST API. Higher-level concerns — concurrency, retries, queuing, backoff, circuit breaking, batch processing — are the consumer's responsibility. The SDK doesn't make those decisions for them.

## Install

```xml
<dependency>
  <groupId>com.scanii</groupId>
  <artifactId>scanii-java</artifactId>
  <version>8.0.0</version>
</dependency>
```

## Quickstart

```java
import com.scanii.ScaniiClient;
import com.scanii.ScaniiClients;
import com.scanii.models.ScaniiProcessingResult;
import java.nio.file.Paths;

ScaniiClient client = ScaniiClients.createDefault("your-api-key", "your-api-secret");
ScaniiProcessingResult result = client.process(Paths.get("/path/to/file"));
System.out.printf("findings: %s%n", result.getFindings());
```

## Regional endpoints

| Constant | Endpoint |
|---|---|
| `ScaniiTarget.AUTO` | `https://api.scanii.com` |
| `ScaniiTarget.US1` | `https://api-us1.scanii.com` |
| `ScaniiTarget.EU1` | `https://api-eu1.scanii.com` |
| `ScaniiTarget.EU2` | `https://api-eu2.scanii.com` |
| `ScaniiTarget.AP1` | `https://api-ap1.scanii.com` |
| `ScaniiTarget.AP2` | `https://api-ap2.scanii.com` |
| `ScaniiTarget.CA1` | `https://api-ca1.scanii.com` |

## Local development with scanii-cli

Run integration tests against a local mock server — no real credentials needed:

```bash
docker run -d --name scanii-cli -p 4000:4000 ghcr.io/scanii/scanii-cli:latest server
mvn verify
```

Test credentials: key `key`, secret `secret`, endpoint `http://localhost:4000`.

## Migration from com.uvasoftware:scanii-java

Replace the Maven coordinates and rename imports:

```
com.uvasoftware.scanii  →  com.scanii
```

The old artifact `com.uvasoftware:scanii-java` is deprecated and will not receive further updates.

## License

Apache 2.0 — see [LICENSE](LICENSE).
