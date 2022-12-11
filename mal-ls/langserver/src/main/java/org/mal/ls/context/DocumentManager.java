package org.mal.ls.context;

public interface DocumentManager {
  boolean isOpen(String uri);

  void open(String uri, String content);

  void update(String uri, String updatedContent);

  void close(String uri);

  String getContent(String uri);
}
