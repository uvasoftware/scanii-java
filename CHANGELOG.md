# Changelog

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
