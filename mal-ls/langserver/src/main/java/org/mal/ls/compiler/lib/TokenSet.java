package org.mal.ls.compiler.lib;

import java.util.Stack;

public enum TokenSet {
  ROOT(
      new TokenType[] { TokenType.CATEGORY, TokenType.ASSOCIATIONS, TokenType.INCLUDE, TokenType.HASH, TokenType.EOF }),
  PAREN(new TokenType[] { TokenType.LPAREN, TokenType.RPAREN }),
  CURLY(new TokenType[] { TokenType.LCURLY, TokenType.RCURLY }),
  NUMBER(new TokenType[] { TokenType.INT, TokenType.FLOAT }),
  TTCEXPR(new TokenType[] { TokenType.PLUS, TokenType.MINUS }),
  TTCTERM(new TokenType[] { TokenType.STAR, TokenType.DIVIDE }),
  TTCPRIM(new TokenType[] { TokenType.ID, TokenType.LPAREN, TokenType.RPAREN, TokenType.INT, TokenType.FLOAT }),
  BLOCK(new TokenType[] { TokenType.LCURLY, TokenType.ID, TokenType.RCURLY }),
  ARROWS(new TokenType[] { TokenType.LARROW, TokenType.RARROW }),
  BRACKETS(new TokenType[] { TokenType.LBRACKET, TokenType.RBRACKET }),
  META(new TokenType[] { TokenType.INFO, TokenType.COLON, TokenType.STRING }),
  EXPR(new TokenType[] { TokenType.UNION, TokenType.INTERSECT, TokenType.MINUS }),
  STEP(new TokenType[] { TokenType.LPAREN, TokenType.RPAREN, TokenType.STAR, TokenType.LBRACKET, TokenType.RBRACKET }),
  MULTIPLICITY(new TokenType[] { TokenType.INT, TokenType.STAR }),
  DEFINE(new TokenType[] { TokenType.COLON, TokenType.STRING }),
  CIA(new TokenType[] { TokenType.C, TokenType.I, TokenType.A }),
  REQUIRES(new TokenType[] { TokenType.REQUIRE, TokenType.COMMA }),
  ATTACKSTEP(new TokenType[] { TokenType.AT, TokenType.LCURLY, TokenType.RCURLY, TokenType.LBRACKET, TokenType.RBRACKET,
      TokenType.REQUIRE, TokenType.INHERIT, TokenType.OVERRIDE }),
  ASSOCIATION(new TokenType[] { TokenType.LBRACKET, TokenType.RBRACKET, TokenType.LARROW, TokenType.RARROW,
      TokenType.LBRACKET, TokenType.RBRACKET }),
  CATEGORYBODY(new TokenType[] { TokenType.ABSTRACT, TokenType.ASSET }), ASSETBODY(new TokenType[] { TokenType.LET,
      TokenType.ALL, TokenType.ANY, TokenType.HASH, TokenType.EXIST, TokenType.NOTEXIST });

  private final TokenType[] types;

  private TokenSet(TokenType[] types) {
    this.types = types;
  }

  public Stack<TokenType> toStack() {
    Stack<TokenType> stack = new Stack<>();
    for (int i = this.types.length - 1; i >= 0; i--) {
      stack.add(this.types[i]);
    }
    return stack;
  }

  public TokenType[] toArray() {
    return this.types;
  }
}
