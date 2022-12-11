package org.mal.ls.context;

import java.util.HashMap;
import java.util.Map;

public class LanguageServerContextImpl implements LanguageServerContext {
  private Map<Key<?>, Object> properties = new HashMap<>();

  @Override
  @SuppressWarnings("unchecked")
  public <V> V get(Key<V> key) {
    return (V) properties.get(key);
  }

  @Override
  public <V> void put(Key<V> key, V value) {
    properties.put(key, value);
  }
}
