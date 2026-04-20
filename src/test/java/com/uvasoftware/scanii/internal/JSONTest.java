package com.uvasoftware.scanii.internal;

import com.uvasoftware.scanii.models.ScaniiAccountInfo;
import com.uvasoftware.scanii.models.ScaniiAuthToken;
import com.uvasoftware.scanii.models.ScaniiPendingResult;
import com.uvasoftware.scanii.models.ScaniiProcessingResult;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
class JSONTest {

  @Nested
  class ParserTest {

    @Test
    void shouldParseEmptyObject() {
      Map<String, Object> result = JSON.load("{}");
      assertTrue(result.isEmpty());
    }

    @Test
    void shouldParseStringValues() {
      Map<String, Object> result = JSON.load("{\"name\": \"hello\"}");
      assertEquals("hello", result.get("name"));
    }

    @Test
    void shouldParseIntegerValues() {
      Map<String, Object> result = JSON.load("{\"count\": 42}");
      assertEquals(42, result.get("count"));
    }

    @Test
    void shouldParseLongValues() {
      Map<String, Object> result = JSON.load("{\"big\": 3000000000}");
      assertEquals(3000000000L, result.get("big"));
    }

    @Test
    void shouldParseNegativeNumbers() {
      Map<String, Object> result = JSON.load("{\"neg\": -7}");
      assertEquals(-7, result.get("neg"));
    }

    @Test
    void shouldParseDoubleValues() {
      Map<String, Object> result = JSON.load("{\"pi\": 3.14}");
      assertEquals(3.14, (double) result.get("pi"), 0.001);
    }

    @Test
    void shouldParseScientificNotation() {
      Map<String, Object> result = JSON.load("{\"val\": 1.5e3}");
      assertEquals(1500.0, (double) result.get("val"), 0.001);
    }

    @Test
    void shouldParseBooleanTrue() {
      Map<String, Object> result = JSON.load("{\"flag\": true}");
      assertEquals(Boolean.TRUE, result.get("flag"));
    }

    @Test
    void shouldParseBooleanFalse() {
      Map<String, Object> result = JSON.load("{\"flag\": false}");
      assertEquals(Boolean.FALSE, result.get("flag"));
    }

    @Test
    void shouldParseNullValue() {
      Map<String, Object> result = JSON.load("{\"nothing\": null}");
      assertTrue(result.containsKey("nothing"));
      assertNull(result.get("nothing"));
    }

    @Test
    void shouldParseEmptyArray() {
      Map<String, Object> result = JSON.load("{\"items\": []}");
      assertEquals(List.of(), result.get("items"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldParseStringArray() {
      Map<String, Object> result = JSON.load("{\"items\": [\"a\", \"b\", \"c\"]}");
      List<Object> items = (List<Object>) result.get("items");
      assertEquals(3, items.size());
      assertEquals("a", items.get(0));
      assertEquals("b", items.get(1));
      assertEquals("c", items.get(2));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldParseNestedObject() {
      Map<String, Object> result = JSON.load("{\"outer\": {\"inner\": \"value\"}}");
      Map<String, Object> outer = (Map<String, Object>) result.get("outer");
      assertEquals("value", outer.get("inner"));
    }

    @Test
    void shouldParseMultipleFields() {
      Map<String, Object> result = JSON.load("{\"a\": 1, \"b\": \"two\", \"c\": true}");
      assertEquals(1, result.get("a"));
      assertEquals("two", result.get("b"));
      assertEquals(Boolean.TRUE, result.get("c"));
    }

    @Test
    void shouldParseEscapedStrings() {
      Map<String, Object> result = JSON.load("{\"msg\": \"hello\\nworld\"}");
      assertEquals("hello\nworld", result.get("msg"));
    }

    @Test
    void shouldParseEscapedQuotes() {
      Map<String, Object> result = JSON.load("{\"msg\": \"say \\\"hi\\\"\"}");
      assertEquals("say \"hi\"", result.get("msg"));
    }

    @Test
    void shouldParseEscapedBackslash() {
      Map<String, Object> result = JSON.load("{\"path\": \"C:\\\\temp\"}");
      assertEquals("C:\\temp", result.get("path"));
    }

    @Test
    void shouldParseUnicodeEscapes() {
      Map<String, Object> result = JSON.load("{\"char\": \"\\u0041\"}");
      assertEquals("A", result.get("char"));
    }

    @Test
    void shouldHandleWhitespace() {
      Map<String, Object> result = JSON.load("  {  \"a\"  :  1  }  ");
      assertEquals(1, result.get("a"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldParseMixedArray() {
      Map<String, Object> result = JSON.load("{\"arr\": [1, \"two\", true, null, 3.5]}");
      List<Object> arr = (List<Object>) result.get("arr");
      assertEquals(5, arr.size());
      assertEquals(1, arr.get(0));
      assertEquals("two", arr.get(1));
      assertEquals(Boolean.TRUE, arr.get(2));
      assertNull(arr.get(3));
      assertEquals(3.5, (double) arr.get(4), 0.001);
    }

    @Test
    void shouldParseEmptyString() {
      Map<String, Object> result = JSON.load("{\"empty\": \"\"}");
      assertEquals("", result.get("empty"));
    }

    @Test
    void shouldIgnoreUnknownFields() {
      // load into a typed model should just skip fields not in the mapping
      ScaniiPendingResult r = JSON.load("{\"id\": \"abc\", \"unknown_field\": 123}", ScaniiPendingResult.class);
      assertEquals("abc", r.getResourceId());
    }

    @Test
    void shouldThrowOnInvalidJson() {
      assertThrows(RuntimeException.class, () -> JSON.load("{invalid}"));
    }

    @Test
    void shouldThrowOnUnterminatedString() {
      assertThrows(RuntimeException.class, () -> JSON.load("{\"key\": \"unterminated}"));
    }

    @Test
    void shouldThrowOnUnexpectedEnd() {
      assertThrows(RuntimeException.class, () -> JSON.load("{\"key\":"));
    }
  }

  @Nested
  class PendingResultTest {

    @Test
    void shouldDeserialize() {
      ScaniiPendingResult r = JSON.load("{\"id\": \"file-abc123\"}", ScaniiPendingResult.class);
      assertEquals("file-abc123", r.getResourceId());
    }

    @Test
    void shouldHandleMissingId() {
      ScaniiPendingResult r = JSON.load("{}", ScaniiPendingResult.class);
      assertNull(r.getResourceId());
    }
  }

  @Nested
  class ProcessingResultTest {

    @Test
    void shouldDeserializeAllFields() {
      String json = "{" +
        "\"id\": \"file-123\"," +
        "\"content_type\": \"application/pdf\"," +
        "\"content_length\": 2048," +
        "\"checksum\": \"sha256abc\"," +
        "\"creation_date\": \"2025-01-15T10:30:00Z\"," +
        "\"findings\": [\"content.malicious.eicar-test-signature\"]," +
        "\"metadata\": {\"foo\": \"bar\", \"baz\": \"qux\"}" +
        "}";

      ScaniiProcessingResult r = JSON.load(json, ScaniiProcessingResult.class);
      assertEquals("file-123", r.getResourceId());
      assertEquals("application/pdf", r.getContentType());
      assertEquals(2048, r.getContentLength());
      assertEquals("sha256abc", r.getChecksum());
      assertEquals(Instant.parse("2025-01-15T10:30:00Z"), r.getCreationDate());
      assertEquals(1, r.getFindings().size());
      assertEquals("content.malicious.eicar-test-signature", r.getFindings().get(0));
      assertEquals("bar", r.getMetadata().get("foo"));
      assertEquals("qux", r.getMetadata().get("baz"));
    }

    @Test
    void shouldDefaultEmptyCollections() {
      ScaniiProcessingResult r = JSON.load("{\"id\": \"x\"}", ScaniiProcessingResult.class);
      assertNotNull(r.getFindings());
      assertTrue(r.getFindings().isEmpty());
      assertNotNull(r.getMetadata());
      assertTrue(r.getMetadata().isEmpty());
    }

    @Test
    void shouldHandleMultipleFindings() {
      String json = "{\"id\": \"x\", \"findings\": [\"a\", \"b\", \"c\"]}";
      ScaniiProcessingResult r = JSON.load(json, ScaniiProcessingResult.class);
      assertEquals(List.of("a", "b", "c"), r.getFindings());
    }

    @Test
    void shouldHandleZeroContentLength() {
      ScaniiProcessingResult r = JSON.load("{\"id\": \"x\", \"content_length\": 0}", ScaniiProcessingResult.class);
      assertEquals(0, r.getContentLength());
    }

    @Test
    void shouldHandleLargeContentLength() {
      ScaniiProcessingResult r = JSON.load("{\"id\": \"x\", \"content_length\": 5000000000}", ScaniiProcessingResult.class);
      assertEquals(5000000000L, r.getContentLength());
    }
  }

  @Nested
  class AuthTokenTest {

    @Test
    void shouldDeserializeAllFields() {
      String json = "{" +
        "\"id\": \"token-xyz\"," +
        "\"creation_date\": \"2025-03-01T08:00:00Z\"," +
        "\"expiration_date\": \"2025-03-01T09:00:00Z\"" +
        "}";

      ScaniiAuthToken r = JSON.load(json, ScaniiAuthToken.class);
      assertEquals("token-xyz", r.getResourceId());
      assertEquals(Instant.parse("2025-03-01T08:00:00Z"), r.getCreationDate());
      assertEquals(Instant.parse("2025-03-01T09:00:00Z"), r.getExpirationDate());
    }

    @Test
    void shouldHandleMissingDates() {
      ScaniiAuthToken r = JSON.load("{\"id\": \"token-1\"}", ScaniiAuthToken.class);
      assertEquals("token-1", r.getResourceId());
      assertNull(r.getCreationDate());
      assertNull(r.getExpirationDate());
    }
  }

  @Nested
  class AccountInfoTest {

    @Test
    void shouldDeserializeAllFields() {
      String json = "{" +
        "\"name\": \"Acme Corp\"," +
        "\"balance\": 9500," +
        "\"starting_balance\": 10000," +
        "\"billing_email\": \"billing@acme.com\"," +
        "\"subscription\": \"pro\"," +
        "\"creation_date\": \"2024-01-01T00:00:00Z\"," +
        "\"modification_date\": \"2025-06-15T12:00:00Z\"," +
        "\"users\": {" +
        "  \"user1@acme.com\": {" +
        "    \"creation_date\": \"2024-01-01T00:00:00Z\"," +
        "    \"last_login_date\": \"2025-06-14T18:30:00Z\"" +
        "  }" +
        "}," +
        "\"keys\": {" +
        "  \"key-abc\": {" +
        "    \"active\": true," +
        "    \"creation_date\": \"2024-02-01T00:00:00Z\"," +
        "    \"last_seen_date\": \"2025-06-15T11:00:00Z\"," +
        "    \"detection_categories_enabled\": [\"malware\", \"pii\"]," +
        "    \"tags\": [\"production\"]" +
        "  }" +
        "}" +
        "}";

      ScaniiAccountInfo r = JSON.load(json, ScaniiAccountInfo.class);
      assertEquals("Acme Corp", r.getName());
      assertEquals(9500, r.getBalance());
      assertEquals(10000, r.getStartingBalance());
      assertEquals("billing@acme.com", r.getBillingEmail());
      assertEquals("pro", r.getSubscription());
      assertEquals(Instant.parse("2024-01-01T00:00:00Z"), r.getCreationDate());
      assertEquals(Instant.parse("2025-06-15T12:00:00Z"), r.getModificationDate());

      // users
      assertNotNull(r.getUsers());
      assertEquals(1, r.getUsers().size());
      ScaniiAccountInfo.User user = r.getUsers().get("user1@acme.com");
      assertNotNull(user);
      assertEquals(Instant.parse("2024-01-01T00:00:00Z"), user.getCreationDate());
      assertEquals(Instant.parse("2025-06-14T18:30:00Z"), user.getLastLoginDate());

      // keys
      assertNotNull(r.getKeys());
      assertEquals(1, r.getKeys().size());
      ScaniiAccountInfo.ApiKey key = r.getKeys().get("key-abc");
      assertNotNull(key);
      assertTrue(key.isActive());
      assertEquals(Instant.parse("2024-02-01T00:00:00Z"), key.getCreationDate());
      assertEquals(Instant.parse("2025-06-15T11:00:00Z"), key.getLastSeenDate());
      assertTrue(key.getDetectionCategoriesEnabled().contains("malware"));
      assertTrue(key.getDetectionCategoriesEnabled().contains("pii"));
      assertEquals(1, key.getTags().size());
      assertTrue(key.getTags().contains("production"));
    }

    @Test
    void shouldHandleInactiveKey() {
      String json = "{\"name\": \"test\", \"balance\": 0, \"starting_balance\": 0," +
        "\"keys\": {\"k1\": {\"active\": false, \"detection_categories_enabled\": [], \"tags\": []}}}";

      ScaniiAccountInfo r = JSON.load(json, ScaniiAccountInfo.class);
      assertFalse(r.getKeys().get("k1").isActive());
      assertTrue(r.getKeys().get("k1").getDetectionCategoriesEnabled().isEmpty());
      assertTrue(r.getKeys().get("k1").getTags().isEmpty());
    }

    @Test
    void shouldHandleMultipleUsersAndKeys() {
      String json = "{\"name\": \"test\", \"balance\": 0, \"starting_balance\": 0," +
        "\"users\": {" +
        "  \"a@x.com\": {\"creation_date\": \"2024-01-01T00:00:00Z\"}," +
        "  \"b@x.com\": {\"creation_date\": \"2024-02-01T00:00:00Z\"}" +
        "}," +
        "\"keys\": {" +
        "  \"k1\": {\"active\": true, \"detection_categories_enabled\": [], \"tags\": []}," +
        "  \"k2\": {\"active\": false, \"detection_categories_enabled\": [], \"tags\": []}" +
        "}}";

      ScaniiAccountInfo r = JSON.load(json, ScaniiAccountInfo.class);
      assertEquals(2, r.getUsers().size());
      assertEquals(2, r.getKeys().size());
    }

    @Test
    void shouldHandleMissingUsersAndKeys() {
      String json = "{\"name\": \"test\", \"balance\": 100, \"starting_balance\": 200}";
      ScaniiAccountInfo r = JSON.load(json, ScaniiAccountInfo.class);
      assertNull(r.getUsers());
      assertNull(r.getKeys());
    }
  }

  @Nested
  class ErrorExtractorTest {

    @Test
    void shouldExtractErrorField() {
      Map<String, Object> result = JSON.load("{\"error\": \"something went wrong\"}");
      assertTrue(result.containsKey("error"));
      assertEquals("something went wrong", result.get("error").toString());
    }

    @Test
    void shouldHandleNoErrorField() {
      Map<String, Object> result = JSON.load("{\"status\": \"ok\"}");
      assertFalse(result.containsKey("error"));
    }
  }

  @Test
  void shouldThrowOnUnsupportedType() {
    assertThrows(IllegalArgumentException.class, () -> JSON.load("{}", String.class));
  }
}
