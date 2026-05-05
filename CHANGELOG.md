# Changelog

## [8.2.0] — 2026-05-05

### Deprecated

- `ScaniiTarget.AUTO` — latency-based routing (`https://api.scanii.com`) does not guarantee
  regional data placement. Use an explicit regional constant (`ScaniiTarget.US1`,
  `ScaniiTarget.EU1`, etc.) instead. Will be removed in a future major version.
- `ScaniiClients.createDefault(String key, String secret)` — defaults to `ScaniiTarget.AUTO`.
  Use `createDefault(ScaniiTarget, String, String)` with an explicit target instead.
  Will be removed in a future major version.
- Constructing a client via the builder without calling `.target(...)` now logs a deprecation
  warning to `System.err` at runtime.

## [8.1.0] — 2026-05-01

### Added

- `retrieveTrace(String id)` — retrieves an ordered list of processing events for a given result id
  (`GET /v2.2/files/{id}/trace`). Returns `Optional<ScaniiTraceResult>`, empty on 404.
  Preview: the trace endpoint may shift before being marked stable.
- `processFromUrl(URI location)` / `processFromUrl(URI location, Map<String,String> metadata)` —
  submits a remote URL for synchronous processing (`POST /v2.2/files` with `location` field).
- `ScaniiTraceResult` model with inner `ScaniiTraceEvent` (timestamp, message).

### Deprecated

- `ScaniiProcessingResult.getError()` / `setError()` — the `error` field in the JSON response is
  deprecated in the v2.2 spec. Error conditions are signalled via `ScaniiException`. Will be removed
  in a future major version.

## [8.0.0] — 2026-04-23

### Breaking changes

- **groupId renamed:** `com.uvasoftware` → `com.scanii`
- **Package renamed:** `com.uvasoftware.scanii` → `com.scanii`
- **Java minimum version raised:** Java 11 → Java 21 LTS

### Changes

- Rebranded to `com.scanii:scanii-java`
- Java source and target updated to 21
- Integration tests now run against [scanii-cli](https://github.com/scanii/scanii-cli) — no real credentials required
- CI matrix: Java 21 and 25 across Ubuntu, macOS, and Windows
- JUnit 5.12.2

### Migration

Update your Maven dependency:

```xml
<!-- before -->
<dependency>
  <groupId>com.uvasoftware</groupId>
  <artifactId>scanii-java</artifactId>
  <version>7.x.x</version>
</dependency>

<!-- after -->
<dependency>
  <groupId>com.scanii</groupId>
  <artifactId>scanii-java</artifactId>
  <version>8.0.0</version>
</dependency>
```

Then update your imports:
```
import com.uvasoftware.scanii.*  →  import com.scanii.*
```

The old `com.uvasoftware:scanii-java` coordinates are deprecated. See the migration guide at https://github.com/scanii/scanii-java.
