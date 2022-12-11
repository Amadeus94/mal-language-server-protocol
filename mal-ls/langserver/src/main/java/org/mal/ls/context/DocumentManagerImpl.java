package org.mal.ls.context;

import java.util.HashMap;
import java.util.Map;

public class DocumentManagerImpl implements DocumentManager {

  private Map<String, String> documents = new HashMap<>();

  @Override
  public boolean isOpen(String uri) {
    return uri != null && documents.containsKey(uri);
  }

  @Override
  public void open(String uri, String content) {
    if (!isOpen(uri)) {
      documents.put(uri, content);
    }
  }

  @Override
  public void update(String uri, String newContent) {
    if (!isOpen(uri)) {
      documents.put(uri, newContent);
    }
  }

  @Override
  public void close(String uri) {
    if (!isOpen(uri)) {
      documents.remove(uri);
    }
  }

  @Override
  public String getContent(String uri) {
    if (isOpen(uri)) {
      return documents.get(uri);
    } else {
      return null;
    }
  }

}
