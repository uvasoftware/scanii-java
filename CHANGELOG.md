# Changelog
## v4.1.0
* Starting deploying to GH packages alongside maven central
## v4.0.0
* Dropped support for protocol versions older than 2.1
* Dropped batch client (it wasn't getting used, and it's simple to build your own)
* Refactored ScaniiTarget to allow for custom targets. ScaniiTarget.latest() should be replaced with ScaniiTarget.AUTO
* Added method to create a client without passing a specific target
* Dropped usage of Google Guava
* Migrated testing to GHE Actions (and expanded coverage)
* Batch client creation now includes tuning of the underlying HTTP client 
