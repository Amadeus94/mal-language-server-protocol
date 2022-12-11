package org.mal.ls.compiler.lib;

public class SkippedToken extends Token {
  public SkippedToken(TokenType type, MalLocation location) {
    super(type, location);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Skipped token: ");
    sb.append(type);
    sb.append(", ");
    sb.append(locationString());
    return sb.toString();
  }
}
