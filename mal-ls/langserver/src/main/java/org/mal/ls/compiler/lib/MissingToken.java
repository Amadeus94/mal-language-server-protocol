package org.mal.ls.compiler.lib;

public class MissingToken extends Token {
  public MissingToken(TokenType type, MalLocation location) {
    super(type, location);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Missing token: ");
    sb.append(type);
    sb.append(", ");
    sb.append(locationString());
    return sb.toString();
  }
}
