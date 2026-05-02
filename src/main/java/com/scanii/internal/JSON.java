package com.scanii.internal;

import com.scanii.models.ScaniiAccountInfo;
import com.scanii.models.ScaniiAuthToken;
import com.scanii.models.ScaniiPendingResult;
import com.scanii.models.ScaniiProcessingResult;
import com.scanii.models.ScaniiTraceResult;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

class JSON {

  private static final Map<Class<?>, Function<Map<String, Object>, ?>> READERS = new HashMap<>();

  static {
    READERS.put(ScaniiPendingResult.class, JSON::toPendingResult);
    READERS.put(ScaniiProcessingResult.class, JSON::toProcessingResult);
    READERS.put(ScaniiAuthToken.class, JSON::toAuthToken);
    READERS.put(ScaniiAccountInfo.class, JSON::toAccountInfo);
    READERS.put(ScaniiTraceResult.class, JSON::toTraceResult);
  }

  @SuppressWarnings("unchecked")
  static <T> T load(String json, Class<T> type) {
    Function<Map<String, Object>, ?> reader = READERS.get(type);
    if (reader == null) {
      throw new IllegalArgumentException("unsupported type: " + type);
    }
    Map<String, Object> map = parseObject(json);
    return (T) reader.apply(map);
  }

  static Map<String, Object> load(String json) {
    return parseObject(json);
  }

  // --- model mapping ---

  private static ScaniiPendingResult toPendingResult(Map<String, Object> m) {
    ScaniiPendingResult r = new ScaniiPendingResult();
    r.setResourceId(str(m, "id"));
    return r;
  }

  private static ScaniiProcessingResult toProcessingResult(Map<String, Object> m) {
    ScaniiProcessingResult r = new ScaniiProcessingResult();
    r.setResourceId(str(m, "id"));
    r.setContentType(str(m, "content_type"));
    r.setContentLength(lng(m, "content_length"));
    r.setChecksum(str(m, "checksum"));
    r.setCreationDate(instant(m, "creation_date"));
    r.setFindings(strList(m, "findings"));
    r.setMetadata(strMap(m, "metadata"));
    r.setError(str(m, "error"));
    return r;
  }

  @SuppressWarnings("unchecked")
  private static ScaniiTraceResult toTraceResult(Map<String, Object> m) {
    ScaniiTraceResult r = new ScaniiTraceResult();
    r.setResourceId(str(m, "id"));
    List<Object> eventsRaw = (List<Object>) m.get("events");
    if (eventsRaw != null) {
      List<ScaniiTraceResult.ScaniiTraceEvent> events = new ArrayList<>(eventsRaw.size());
      for (Object o : eventsRaw) {
        Map<String, Object> em = (Map<String, Object>) o;
        ScaniiTraceResult.ScaniiTraceEvent e = new ScaniiTraceResult.ScaniiTraceEvent();
        e.setMessage(str(em, "message"));
        String ts = str(em, "timestamp");
        if (ts != null) e.setTimestamp(Instant.parse(ts));
        events.add(e);
      }
      r.setEvents(events);
    }
    return r;
  }

  private static ScaniiAuthToken toAuthToken(Map<String, Object> m) {
    ScaniiAuthToken r = new ScaniiAuthToken();
    r.setResourceId(str(m, "id"));
    r.setCreationDate(instant(m, "creation_date"));
    r.setExpirationDate(instant(m, "expiration_date"));
    return r;
  }

  @SuppressWarnings("unchecked")
  private static ScaniiAccountInfo toAccountInfo(Map<String, Object> m) {
    ScaniiAccountInfo r = new ScaniiAccountInfo();
    r.setName(str(m, "name"));
    r.setBalance(lng(m, "balance"));
    r.setStartingBalance(lng(m, "starting_balance"));
    r.setBillingEmail(str(m, "billing_email"));
    r.setSubscription(str(m, "subscription"));
    r.setCreationDate(instant(m, "creation_date"));
    r.setModificationDate(instant(m, "modification_date"));

    Map<String, Object> usersRaw = (Map<String, Object>) m.get("users");
    if (usersRaw != null) {
      Map<String, ScaniiAccountInfo.User> users = new LinkedHashMap<>();
      usersRaw.forEach((k, v) -> {
        Map<String, Object> um = (Map<String, Object>) v;
        ScaniiAccountInfo.User u = new ScaniiAccountInfo.User();
        u.setCreationDate(instant(um, "creation_date"));
        u.setLastLoginDate(instant(um, "last_login_date"));
        users.put(k, u);
      });
      r.setUsers(users);
    }

    Map<String, Object> keysRaw = (Map<String, Object>) m.get("keys");
    if (keysRaw != null) {
      Map<String, ScaniiAccountInfo.ApiKey> keys = new LinkedHashMap<>();
      keysRaw.forEach((k, v) -> {
        Map<String, Object> km = (Map<String, Object>) v;
        ScaniiAccountInfo.ApiKey ak = new ScaniiAccountInfo.ApiKey();
        ak.setActive(bool(km, "active"));
        ak.setCreationDate(instant(km, "creation_date"));
        ak.setLastSeenDate(instant(km, "last_seen_date"));
        ak.setDetectionCategoriesEnabled(strSet(km, "detection_categories_enabled"));
        ak.setTags(strSet(km, "tags"));
        keys.put(k, ak);
      });
      r.setKeys(keys);
    }

    return r;
  }

  // --- field helpers ---

  private static String str(Map<String, Object> m, String key) {
    Object v = m.get(key);
    return v == null ? null : v.toString();
  }

  private static long lng(Map<String, Object> m, String key) {
    Object v = m.get(key);
    if (v == null) return 0L;
    if (v instanceof Number) return ((Number) v).longValue();
    return Long.parseLong(v.toString());
  }

  private static boolean bool(Map<String, Object> m, String key) {
    Object v = m.get(key);
    if (v == null) return false;
    if (v instanceof Boolean) return (Boolean) v;
    return Boolean.parseBoolean(v.toString());
  }

  private static Instant instant(Map<String, Object> m, String key) {
    String v = str(m, key);
    return v == null ? null : Instant.parse(v);
  }

  @SuppressWarnings("unchecked")
  private static List<String> strList(Map<String, Object> m, String key) {
    Object v = m.get(key);
    if (v == null) return new ArrayList<>();
    List<Object> list = (List<Object>) v;
    List<String> result = new ArrayList<>(list.size());
    for (Object o : list) {
      result.add(o == null ? null : o.toString());
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, String> strMap(Map<String, Object> m, String key) {
    Object v = m.get(key);
    if (v == null) return new HashMap<>();
    Map<String, Object> raw = (Map<String, Object>) v;
    Map<String, String> result = new HashMap<>();
    raw.forEach((k, val) -> result.put(k, val == null ? null : val.toString()));
    return result;
  }

  @SuppressWarnings("unchecked")
  private static Set<String> strSet(Map<String, Object> m, String key) {
    Object v = m.get(key);
    if (v == null) return new LinkedHashSet<>();
    List<Object> list = (List<Object>) v;
    Set<String> result = new LinkedHashSet<>();
    for (Object o : list) {
      result.add(o == null ? null : o.toString());
    }
    return result;
  }

  // --- JSON parser ---

  @SuppressWarnings("unchecked")
  private static Map<String, Object> parseObject(String json) {
    Object result = new Parser(json).parse();
    return (Map<String, Object>) result;
  }

  static final class Parser {
    private final String input;
    private int pos;

    Parser(String input) {
      this.input = input;
      this.pos = 0;
    }

    Object parse() {
      skipWhitespace();
      Object value = readValue();
      skipWhitespace();
      return value;
    }

    private Object readValue() {
      skipWhitespace();
      char c = peek();
      switch (c) {
        case '"': return readString();
        case '{': return readObject();
        case '[': return readArray();
        case 't': case 'f': return readBoolean();
        case 'n': return readNull();
        default: return readNumber();
      }
    }

    private Map<String, Object> readObject() {
      expect('{');
      Map<String, Object> map = new LinkedHashMap<>();
      skipWhitespace();
      if (peek() == '}') {
        advance();
        return map;
      }
      while (true) {
        skipWhitespace();
        String key = readString();
        skipWhitespace();
        expect(':');
        Object value = readValue();
        map.put(key, value);
        skipWhitespace();
        if (peek() == ',') {
          advance();
        } else {
          break;
        }
      }
      skipWhitespace();
      expect('}');
      return map;
    }

    private List<Object> readArray() {
      expect('[');
      List<Object> list = new ArrayList<>();
      skipWhitespace();
      if (peek() == ']') {
        advance();
        return list;
      }
      while (true) {
        list.add(readValue());
        skipWhitespace();
        if (peek() == ',') {
          advance();
        } else {
          break;
        }
      }
      skipWhitespace();
      expect(']');
      return list;
    }

    private String readString() {
      expect('"');
      StringBuilder sb = new StringBuilder();
      while (pos < input.length()) {
        char c = input.charAt(pos++);
        if (c == '"') return sb.toString();
        if (c == '\\') {
          char esc = input.charAt(pos++);
          switch (esc) {
            case '"': sb.append('"'); break;
            case '\\': sb.append('\\'); break;
            case '/': sb.append('/'); break;
            case 'b': sb.append('\b'); break;
            case 'f': sb.append('\f'); break;
            case 'n': sb.append('\n'); break;
            case 'r': sb.append('\r'); break;
            case 't': sb.append('\t'); break;
            case 'u':
              String hex = input.substring(pos, pos + 4);
              sb.append((char) Integer.parseInt(hex, 16));
              pos += 4;
              break;
            default: sb.append(esc);
          }
        } else {
          sb.append(c);
        }
      }
      throw error("unterminated string");
    }

    private Number readNumber() {
      int start = pos;
      if (peek() == '-') advance();
      while (pos < input.length() && Character.isDigit(input.charAt(pos))) advance();
      boolean isDouble = false;
      if (pos < input.length() && input.charAt(pos) == '.') {
        isDouble = true;
        advance();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) advance();
      }
      if (pos < input.length() && (input.charAt(pos) == 'e' || input.charAt(pos) == 'E')) {
        isDouble = true;
        advance();
        if (pos < input.length() && (input.charAt(pos) == '+' || input.charAt(pos) == '-')) advance();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) advance();
      }
      String num = input.substring(start, pos);
      if (isDouble) return Double.parseDouble(num);
      long val = Long.parseLong(num);
      if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) return (int) val;
      return val;
    }

    private Boolean readBoolean() {
      if (input.startsWith("true", pos)) { pos += 4; return Boolean.TRUE; }
      if (input.startsWith("false", pos)) { pos += 5; return Boolean.FALSE; }
      throw error("expected boolean");
    }

    private Object readNull() {
      if (input.startsWith("null", pos)) { pos += 4; return null; }
      throw error("expected null");
    }

    private void skipWhitespace() {
      while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) pos++;
    }

    private char peek() {
      if (pos >= input.length()) throw error("unexpected end of input");
      return input.charAt(pos);
    }

    private void advance() { pos++; }

    private void expect(char c) {
      if (peek() != c) throw error("expected '" + c + "' but got '" + peek() + "'");
      advance();
    }

    private RuntimeException error(String msg) {
      return new RuntimeException("JSON parse error at position " + pos + ": " + msg);
    }
  }
}
