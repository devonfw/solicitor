package com.devonfw.tools.solicitor.model.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TextPoolImpl}.
 */
class TextPoolImplTest {

  @Test
  void testStoreAndRetrieveNull() {

    TextPool pool = new TextPoolImpl();
    assertEquals("NULL_KEY", pool.store(null));

    assertNull(pool.retrieve("NULL_KEY"));
  }

  @Test
  void testThrowExceptionForUnknownKey() {

    TextPool pool = new TextPoolImpl();
    assertThrows(NoSuchElementException.class, () -> pool.retrieve("foo"));
  }

  @Test
  void testThrowExceptionForNullKey() {

    TextPool pool = new TextPoolImpl();
    assertThrows(NullPointerException.class, () -> pool.retrieve(null));
  }

  @Test
  void testStoreAndRetrieveData() {

    TextPool pool = new TextPoolImpl();
    String keyAbc = pool.store("abc");
    String keyDef = pool.store("def");
    assertEquals("abc", pool.retrieve(keyAbc));
    assertEquals("def", pool.retrieve(keyDef));
  }

}
