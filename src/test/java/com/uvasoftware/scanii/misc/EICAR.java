package com.uvasoftware.scanii.misc;

import java.util.Base64;

public class EICAR {
  public static final String SIGNATURE_BASE64 = "WDVPIVAlQEFQWzRcUFpYNTQoUF4pN0NDKTd9JEVJQ0FSLVNUQU5EQVJELUFOVElWSVJVUy1URVNULUZJTEUhJEgrSCo=";

  public static byte[] decode() {
    return Base64.getDecoder().decode(SIGNATURE_BASE64);
  }
}
