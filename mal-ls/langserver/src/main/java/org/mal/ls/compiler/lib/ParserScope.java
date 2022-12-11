package org.mal.ls.compiler.lib;

import java.util.Stack;

public class ParserScope {
  private Stack<TokenType> scope;
  private final ParserScope parent;

  // Root scope, single token
  public ParserScope(TokenSet set) {
    this.parent = null;
    this.scope = set.toStack();
  }

  // Root scope, set of tokens
  public ParserScope(TokenType type) {
    this.parent = null;
    this.scope = new Stack<>();
    scope.push(type);
  }

  // Scope expects a set of tokens
  public ParserScope(TokenSet set, ParserScope parent) {
    this.parent = parent;
    this.scope = set.toStack();
  }

  // Scope expects single token
  public ParserScope(TokenType type, ParserScope parent) {
    this.parent = parent;
    this.scope = new Stack<>();
    scope.push(type);
  }

  public boolean contains(TokenType type) {
    return this.scope.contains(type);
  }

  public void remove(TokenType type) {
    if (scope.contains(type)) {
      while (scope.peek() != type) {
        scope.pop();
      }
      scope.pop();
    }
  }

  public Object[] getTypes() {
    return scope.toArray();
  }

  public boolean expects(TokenType type) {
    if (scope.contains(type)) {
      return true;
    } else if (parent != null) {
      return parent.expects(type);
    } else {
      return false;
    }
  }
}
