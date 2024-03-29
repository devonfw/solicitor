package com.devonfw.tools.solicitor.model.impl;

import java.util.NoSuchElementException;

/**
 * Holds texts in a pool (effectively a map) and avoids storing the same text multiple times. This can be used to
 * minimize storage requirements when persisting a large amount of (possibly duplicated) texts.
 */
public interface TextPool {

  /**
   * Store a text in the pool.
   *
   * @param text the text to store in the pool; might be <code>null</code>
   * @return the key of the text in the pool; might be <code>null</code> (which is returned for a stored
   *         <code>null</code> value)
   */
  String store(String text);

  /**
   * Retrieves a text from the pool
   *
   * @param key the key of the text (might <code>null</code> which represents a <code>null</code> value)
   * @return the stored text; might be <code>null</code> if a null value was stored in the pool
   * @throws NoSuchElementException if nothing is stored under the given key
   */
  String retrieve(String key);
}
