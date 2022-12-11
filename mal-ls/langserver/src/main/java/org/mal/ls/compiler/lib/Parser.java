/*
 * Copyright 2019 Foreseeti AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mal.ls.compiler.lib;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class Parser {
  private MalDiagnosticLogger LOGGER;
  private AST ast;
  private Lexer lex;
  private Token tok;
  private Token prev;
  private Set<File> included;
  private File currentFile;
  private Path originPath;

  private Parser(File file) throws IOException {
    Locale.setDefault(Locale.ROOT);
    var canonicalFile = file.getCanonicalFile();
    LOGGER = MalDiagnosticLogger.getInstance();
    this.ast = new AST(file.toURI().toString());
    this.lex = new Lexer(canonicalFile);
    this.included = new HashSet<File>();
    this.included.add(canonicalFile);
    this.currentFile = canonicalFile;
    this.originPath = Path.of(canonicalFile.getParent());
  }

  private Parser(File file, Path originPath, Set<File> included) throws IOException {
    Locale.setDefault(Locale.ROOT);
    LOGGER = MalDiagnosticLogger.getInstance();
    this.ast = new AST(file.toURI().toString());
    this.lex = new Lexer(file, originPath.relativize(Path.of(file.getPath())).toString());
    this.included = included;
    this.included.add(file);
    this.currentFile = file;
    this.originPath = originPath;
  }

  public static AST parse(String uri) throws IOException, URISyntaxException {
    return parse(new File(new URI(uri)));
  }

  public static AST parse(File file) throws IOException {
    return new Parser(file)._parse();
  }

  private static AST parse(File file, Path originPath, Set<File> included) throws IOException {
    return new Parser(file, originPath, included)._parse();
  }

  private void _next() {
    prev = tok;
    tok = lex.next();
  }

  private void _skip() {
    tok = lex.next();
  }

  private void expected(TokenType type) {
    LOGGER.error(prev, String.format("Syntax error on token '%s', expected %s after this token", prev, type));
  }

  private void expected(String expected) {
    LOGGER.error(prev, String.format("Syntax error on token '%s', expected %s after this token", prev, expected));
  }

  private void skipNotInScope(ParserScope scope) {
    List<Token> skipped = new ArrayList<>();
    while (!scope.expects(tok.type)) {
      skipped.add(tok);
      _skip();
    }
    if (!skipped.isEmpty()) {
      if (skipped.size() > 1) {
        StringBuilder sb = new StringBuilder();
        sb.append("Syntax error on tokens ");
        for (int i = 0; i < skipped.size(); i++) {
          sb.append(String.format("'%s'", skipped.get(i)));
          if (i == skipped.size() - 2) {
            sb.append(" and ");
          } else {
            sb.append(", ");
          }
        }
        sb.append("delete these tokens.");
        LOGGER.error(new MalLocation(tok.getUri(), skipped.get(0).getStart(), skipped.get(skipped.size() - 1).getEnd()),
            sb.toString());
      } else {
        LOGGER.error(skipped.get(0), String.format("Syntax error on token '%s', delete this token.", skipped.get(0)));
      }
    }
  }

  // eats token and removes it from current context.
  private Token eat(TokenType type, ParserScope scope) {
    Token token = eat2(type, scope);
    scope.remove(type);
    return token;
  }

  // eat without removing from context.
  private Token eat2(TokenType type, ParserScope scope) {
    skipNotInScope(scope);
    Token token = this.tok;
    if (token.type == type) {
      _next();
      return token;
    }
    expected(type);
    return new MissingToken(type, prev);
  }

  private Token eatEitherFromScope(ParserScope scope, String expected) {
    skipNotInScope(scope);
    if (scope.contains(tok.type)) {
      return tok;
    }
    expected(expected);
    // TODO tokentype
    return new MissingToken(TokenType.UNRECOGNIZEDTOKEN, prev);
  }

  private Token eatOptional(TokenType type) {
    Token token = this.tok;
    if (token.type == type) {
      _next();
      return token;
    }
    return null;
  }

  // <mal> ::= (<category> | <associations> | <include> | <define>)* EOF
  private AST _parse() {
    ParserScope scope = new ParserScope(TokenSet.ROOT);
    _next();
    while (true) {
      Token token = tok;
      switch (tok.type) {
        case CATEGORY:
          _next();
          ast.addCategory(_parseCategory(scope, token));
          break;
        case ASSOCIATIONS:
          _next();
          ast.addAssociations(_parseAssociations(scope));
          break;
        case INCLUDE:
          _next();
          ast.include(_parseInclude(scope));
          break;
        case HASH:
          _next();
          ast.addDefine(_parseDefine(scope, token));
          break;
        case EOF:
          return ast;
        default:
          LOGGER.error(tok, String.format("Syntax error on token '%s', delete this token", tok));
          _next();
      }
    }
  }

  // ID
  private AST.ID _parseID(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenType.ID, parentScope);
    var id = eat(TokenType.ID, scope);
    return new AST.ID(id);
  }

  // <define> ::= HASH ID COLON STRING
  private AST.Define _parseDefine(ParserScope parentScope, Token firstToken) {
    ParserScope scope = new ParserScope(TokenSet.DEFINE, parentScope);
    var id = _parseID(scope);
    eat(TokenType.COLON, scope);
    var string = eat(TokenType.STRING, scope);

    AST.Define define = new AST.Define(firstToken, id, string);

    return define;
  }

  // <meta> ::= INFO COLON STRING
  private AST.Meta _parseMeta(ParserScope parentScope, AST.ID type) {
    ParserScope scope = new ParserScope(TokenSet.META, parentScope);
    eat(TokenType.INFO, scope);
    eat(TokenType.COLON, scope);
    var string = eat(TokenType.STRING, scope);
    return new AST.Meta(type, type, string);
  }

  // <meta>*
  private List<AST.Meta> _parseMetaList(ParserScope parentScope) {
    var meta = new ArrayList<AST.Meta>();
    ParserScope scope = new ParserScope(TokenType.ID, parentScope);
    while (tok.type == TokenType.ID) {
      var type = new AST.ID(tok);
      _next();
      meta.add(_parseMeta(scope, type));
    }
    return meta;
  }

  // <include> ::= INCLUDE STRING
  private AST _parseInclude(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenType.STRING, parentScope);
    var string = eat(TokenType.STRING, scope);

    var filename = string.stringValue;
    var file = new File(filename);
    if (!file.isAbsolute()) {
      var currentDir = currentFile.getParent();
      file = new File(String.format("%s/%s", currentDir, filename));
    }
    try {
      file = file.getCanonicalFile();
    } catch (IOException e) {
      LOGGER.error(string, "Syntax error, could not find specified file.");
      return new AST(file.toURI().toString());
    }
    if (included.contains(file)) {
      LOGGER.warn(string, "Duplicate include.");
      return new AST(file.toURI().toString());

    } else {
      try {
        return Parser.parse(file, originPath, included);
      } catch (IOException e) {
        LOGGER.error(string, "Syntax error, could not find specified file.");
        return new AST(file.toURI().toString());
      }
    }
  }

  // <number> ::= INT | FLOAT
  private double _parseNumber(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenSet.NUMBER);
    Token token = eatEitherFromScope(scope, "a number");
    double val = 0.0;
    switch (token.type) {
      case INT:
        val = tok.intValue;
        _next();
        return val;
      case FLOAT:
        val = tok.doubleValue;
        _next();
        return val;
      default:
        return 0;
    }
  }

  // <category> ::= CATEGORY ID <meta1>* LCURLY <asset>* RCURLY
  private AST.Category _parseCategory(ParserScope parentScope, Token firstToken) {
    ParserScope scope = new ParserScope(TokenSet.CURLY, parentScope);
    var name = _parseID(scope);
    var meta = _parseMetaList(scope);
    eat(TokenType.LCURLY, scope);
    var assets = _parseAssetList(scope);
    eat(TokenType.RCURLY, scope);

    return new AST.Category(firstToken, name, meta, assets);
  }

  // <asset> ::=
  // ABSTRACT? ASSET ID (EXTENDS ID)? <meta1>* LCURLY (<attackstep> | <variable>)*
  // RCURLY
  private AST.Asset _parseAsset(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenSet.CURLY, parentScope);

    var isAbstract = false;
    var modifier = Optional.ofNullable(eatOptional(TokenType.ABSTRACT));
    if (modifier.isPresent()) {
      isAbstract = true;
    }

    var asset = eat2(TokenType.ASSET, scope);
    var name = _parseID(scope);

    Optional<AST.ID> parent = Optional.empty();
    if (tok.type == TokenType.EXTENDS) {
      _next();
      parent = Optional.of(_parseID(scope));
    }
    var meta = _parseMetaList(scope);

    eat(TokenType.LCURLY, scope);

    var attackSteps = new ArrayList<AST.AttackStep>();
    var variables = new ArrayList<AST.Variable>();
    _parseAssetBody(parentScope, attackSteps, variables);

    eat(TokenType.RCURLY, scope);
    return new AST.Asset(asset, isAbstract, name, parent, meta, attackSteps, variables);
  }

  private void _parseAssetBody(ParserScope parentScope, List<AST.AttackStep> attackSteps,
      List<AST.Variable> variables) {
    ParserScope scope = new ParserScope(TokenSet.ASSETBODY, parentScope);
    loop: while (true) {
      Token token = tok;
      switch (tok.type) {
        case LET:
          _next();
          variables.add(_parseVariable(scope, token));
          break;
        case ALL:
        case ANY:
        case HASH:
        case EXIST:
        case NOTEXIST:
          attackSteps.add(_parseAttackStep(_parseAttackStepType(), scope));
          break;
        default:
          if (scope.expects(tok.type)) {
            break loop;
          } else {
            LOGGER.error(tok, String.format("Syntax error on token '%s', delete this token", tok));
            _next();
            break;
          }
      }
    }
  }

  private List<AST.Asset> _parseAssetList(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenSet.CATEGORYBODY, parentScope);
    var assets = new ArrayList<AST.Asset>();
    while (true) {
      switch (tok.type) {
        case ABSTRACT:
        case ASSET:
          assets.add(_parseAsset(scope));
          break;
        default:
          if (scope.expects(tok.type)) {
            return assets;
          } else {
            LOGGER.error(tok, String.format("Syntax error on token '%s', delete this token", tok));
            _next();
            break;
          }
      }
    }
  }

  // <attackstep> ::= <astype> ID <tag>* <cia>? <ttc>? <meta1>* <existence>?
  // <reaches>?
  private AST.AttackStep _parseAttackStep(AST.AttackStepType asType, ParserScope parentScope) {
    var firstToken = tok;
    ParserScope scope = new ParserScope(TokenSet.ATTACKSTEP, parentScope);

    var name = _parseID(scope);
    List<AST.ID> tags = new ArrayList<>();
    while (tok.type == TokenType.AT) {
      _next();
      tags.add(_parseTag(scope));
    }
    Optional<List<AST.CIA>> cia = Optional.empty();
    if (tok.type == TokenType.LCURLY) {
      cia = Optional.ofNullable(_parseCIA(scope));
    }

    Optional<AST.TTCExpr> ttc = Optional.empty();
    if (tok.type == TokenType.LBRACKET) {
      ttc = _parseTTC(scope);
    }
    var meta = _parseMetaList(scope);
    Optional<AST.Requires> requires = Optional.ofNullable(_parseExistence(scope));
    Optional<AST.Reaches> reaches = Optional.ofNullable(_parseReaches(scope));
    return new AST.AttackStep(firstToken, asType, name, tags, cia, ttc, meta, requires, reaches);
  }

  // <astype> ::= ALL | ANY | HASH | EXIST | NOTEXIST
  // TODO handle null
  private AST.AttackStepType _parseAttackStepType() {
    switch (tok.type) {
      case ALL:
        _next();
        return AST.AttackStepType.ALL;
      case ANY:
        _next();
        return AST.AttackStepType.ANY;
      case HASH:
        _next();
        return AST.AttackStepType.DEFENSE;
      case EXIST:
        _next();
        return AST.AttackStepType.EXIST;
      case NOTEXIST:
        _next();
        return AST.AttackStepType.NOTEXIST;
      default:
        return null;
    }
  }

  // <tag> ::= AT ID
  private AST.ID _parseTag(ParserScope parentScope) {
    var id = _parseID(parentScope);
    return id;
  }

  // <cia> ::= LCURLY <cia-list>? RCURLY
  private List<AST.CIA> _parseCIA(ParserScope parentScope) {
    eat(TokenType.LCURLY, parentScope);
    List<AST.CIA> cia = new ArrayList<AST.CIA>();
    _parseCIAList(cia, parentScope);
    eat(TokenType.RCURLY, parentScope);
    return cia;
  }

  // <cia-list> ::= <cia-class> (COMMA <cia-class>)*
  private void _parseCIAList(List<AST.CIA> cia, ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenType.COMMA, parentScope);
    _parseCIAClass(cia, scope);
    while (tok.type == TokenType.COMMA) {
      _next();
      _parseCIAClass(cia, scope);
    }
  }

  // <cia-class> ::= C | I | A
  private void _parseCIAClass(List<AST.CIA> cia, ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenSet.CIA, parentScope);
    Token token = eatEitherFromScope(scope, "C, I or A");
    switch (token.type) {
      case C:
        cia.add(AST.CIA.C);
        _next();
        break;
      case I:
        cia.add(AST.CIA.I);
        _next();
        break;
      case A:
        cia.add(AST.CIA.A);
        _next();
        break;
      default:
        break;
    }
  }

  // <ttc> ::= LBRACKET <ttc-expr>? RBRACKET
  private Optional<AST.TTCExpr> _parseTTC(ParserScope parentScope) {
    eat(TokenType.LBRACKET, parentScope);
    Optional<AST.TTCExpr> expr = Optional.empty();
    if (tok.type != TokenType.RBRACKET) {
      expr = Optional.of(_parseTTCExpr(parentScope));
    } else {
      // empty brackets [] = 0
      expr = Optional.of(new AST.TTCFuncExpr(tok, new AST.ID(tok, "Zero"), new ArrayList<>()));
    }
    eat(TokenType.RBRACKET, parentScope);
    return expr;
  }

  // <ttc-expr> ::= <ttc-term> ((PLUS | MINUS) <ttc-term>)*
  private AST.TTCExpr _parseTTCExpr(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenSet.TTCEXPR, parentScope);
    var firstToken = tok;
    var lhs = _parseTTCTerm(scope);
    while (tok.type == TokenType.PLUS || tok.type == TokenType.MINUS) {
      var addType = tok.type;
      _next();
      var rhs = _parseTTCTerm(scope);
      if (addType == TokenType.PLUS) {
        lhs = new AST.TTCAddExpr(firstToken, lhs, rhs);
      } else {
        lhs = new AST.TTCSubExpr(firstToken, lhs, rhs);
      }
    }
    return lhs;
  }

  // <ttc-term> ::= <ttc-fact> ((STAR | DIVIDE) <ttc-fact>)*
  private AST.TTCExpr _parseTTCTerm(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenSet.TTCTERM, parentScope);

    var firstToken = tok;

    var lhs = _parseTTCFact(scope);
    while (tok.type == TokenType.STAR || tok.type == TokenType.DIVIDE) {
      var mulType = tok.type;
      _next();
      var rhs = _parseTTCFact(scope);
      if (mulType == TokenType.STAR) {
        lhs = new AST.TTCMulExpr(firstToken, lhs, rhs);
      } else {
        lhs = new AST.TTCDivExpr(firstToken, lhs, rhs);
      }
    }
    return lhs;
  }

  // <ttc-fact> ::= <ttc-prim> (POWER <ttc-fact>)?
  private AST.TTCExpr _parseTTCFact(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenType.POWER, parentScope);
    var firstToken = tok;

    var e = _parseTTCPrim(scope);
    if (tok.type == TokenType.POWER) {
      _next();
      e = new AST.TTCPowExpr(firstToken, e, _parseTTCFact(scope));
    }
    return e;
  }

  // <ttc-prim> ::= ID (LPAREN (<number> (COMMA <number>)*)? RPAREN)?
  // | LPAREN <ttc-expr> RPAREN | <number>
  private AST.TTCExpr _parseTTCPrim(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenSet.TTCPRIM, parentScope);

    var firstToken = eatEitherFromScope(scope, "an expression");
    if (tok.type == TokenType.ID) {
      var function = _parseID(scope);
      var params = new ArrayList<Double>();
      if (tok.type == TokenType.LPAREN) {
        eat(TokenType.LPAREN, scope);
        if (tok.type == TokenType.INT || tok.type == TokenType.FLOAT) {
          params.add(_parseNumber(scope));
          while (tok.type == TokenType.COMMA) {
            _next();
            params.add(_parseNumber(scope));
          }
        }
        eat(TokenType.RPAREN, scope);
      }
      return new AST.TTCFuncExpr(firstToken, function, params);
    } else if (tok.type == TokenType.LPAREN) {
      eat(TokenType.LPAREN, scope);
      var e = _parseTTCExpr(scope);
      eat(TokenType.RPAREN, scope);
      return e;
    } else if (tok.type == TokenType.INT || tok.type == TokenType.FLOAT) {
      double num = _parseNumber(scope);
      return new AST.TTCNumExpr(firstToken, num);
    } else {
      return new AST.TTCFuncExpr(tok, new AST.ID(tok, "Zero"), new ArrayList<>());
    }
  }

  // <existence> ::= REQUIRE <expr> (COMMA <expr>)*
  private AST.Requires _parseExistence(ParserScope parentScope) {
    if (tok.type != TokenType.REQUIRE) {
      return null;
    }
    var firstToken = tok;
    _next();
    ParserScope scope = new ParserScope(TokenType.COMMA, parentScope);
    var requires = new ArrayList<AST.Expr>();
    requires.add(_parseExpr(scope));
    while (tok.type == TokenType.COMMA) {
      _next();
      if (parentScope.expects(tok.type)) {
        LOGGER.error(prev, "Syntax error, remove trailing comma.");
        break;
      }
      requires.add(_parseExpr(scope));
    }
    return new AST.Requires(firstToken, requires);
  }

  // <reaches> ::= (INHERIT | OVERRIDE) <expr> (COMMA <expr>)*
  private AST.Reaches _parseReaches(ParserScope parentScope) {
    var firstToken = tok;
    var inherits = false;
    if (tok.type == TokenType.INHERIT) {
      inherits = true;
    } else if (tok.type == TokenType.OVERRIDE) {
      inherits = false;
    } else {
      return null;
    }
    _next();
    ParserScope scope = new ParserScope(TokenType.COMMA, parentScope);
    var reaches = new ArrayList<AST.Expr>();
    reaches.add(_parseExpr(scope));
    while (tok.type == TokenType.COMMA) {
      _next();
      if (parentScope.expects(tok.type)) {
        LOGGER.error(prev, "Syntax error, remove trailing comma.");
        break;
      }
      reaches.add(_parseExpr(scope));
    }
    return new AST.Reaches(firstToken, inherits, reaches);
  }

  // <variable> ::= LET ID ASSIGN <expr>
  private AST.Variable _parseVariable(ParserScope parentScope, Token firstToken) {
    ParserScope scope = new ParserScope(TokenType.ASSIGN, parentScope);
    var name = _parseID(scope);
    eat(TokenType.ASSIGN, scope);
    var e = _parseExpr(scope);
    return new AST.Variable(firstToken, name, e);
  }

  // <expr> ::= <steps> ((UNION | INTERSECT | MINUS) <steps>)*
  private AST.Expr _parseExpr(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenSet.EXPR, parentScope);
    var firstToken = tok;

    var lhs = _parseSteps(scope);
    while (tok.type == TokenType.UNION || tok.type == TokenType.INTERSECT || tok.type == TokenType.MINUS) {
      var setType = tok.type;
      _next();
      var rhs = _parseSteps(scope);
      if (setType == TokenType.UNION) {
        lhs = new AST.UnionExpr(firstToken, lhs, rhs);
      } else if (setType == TokenType.INTERSECT) {
        lhs = new AST.IntersectionExpr(firstToken, lhs, rhs);
      } else {
        lhs = new AST.DifferenceExpr(firstToken, lhs, rhs);
      }
    }
    return lhs;
  }

  // <steps> ::= <step> (DOT <step>)*
  private AST.Expr _parseSteps(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenType.DOT, parentScope);
    var firstToken = tok;

    var lhs = _parseStep(scope);
    while (tok.type == TokenType.DOT) {
      _next();
      var rhs = _parseStep(scope);
      lhs = new AST.StepExpr(firstToken, lhs, rhs);
    }
    return lhs;
  }

  // <step> ::= (LPAREN <expr> RPAREN | ID (LPAREN RPAREN)?) (STAR | <type>)*
  private AST.Expr _parseStep(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenSet.STEP, parentScope);
    Token firstToken = tok;
    AST.Expr e = null;
    if (tok.type == TokenType.LPAREN) {
      eat(TokenType.LPAREN, scope);
      e = _parseExpr(scope);
      eat(TokenType.RPAREN, scope);
    } else {
      var id = _parseID(scope);
      e = _parseCallExpr(id, scope);
    }
    while (tok.type == TokenType.STAR || tok.type == TokenType.LBRACKET) {
      if (tok.type == TokenType.STAR) {
        _next();
        e = new AST.TransitiveExpr(firstToken, e);
      } else if (tok.type == TokenType.LBRACKET) {
        e = new AST.SubTypeExpr(firstToken, e, _parseType(scope));
      }
    }
    return e;
  }

  // ::= ID (LPAREN RPAREN)?)
  private AST.Expr _parseCallExpr(AST.ID id, ParserScope parentScope) {
    AST.Expr e = null;

    if (tok.type == TokenType.LPAREN) {
      eat(TokenType.LPAREN, parentScope);
      eat(TokenType.RPAREN, parentScope);
      e = new AST.CallExpr(id, id);
    } else {
      e = new AST.IDExpr(id, id);
    }
    return e;
  }

  // <associations> ::= ASSOCIATIONS LCURLY <associations1>? RCURLY
  private List<AST.Association> _parseAssociations(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenSet.BLOCK, parentScope);
    eat(TokenType.LCURLY, scope);
    List<AST.Association> assocs = new ArrayList<>();
    assocs = _parseAssociations1(scope);
    eat(TokenType.RCURLY, scope);
    return assocs;
  }

  // <associations1> ::= ID <association> (ID (<meta2> | <association>))*
  private List<AST.Association> _parseAssociations1(ParserScope parentScope) {
    var assocs = new ArrayList<AST.Association>();
    var leftAsset = _parseID(parentScope);
    var assoc = _parseAssociation(parentScope, leftAsset);

    while (tok.type == TokenType.ID) {
      var id = _parseID(parentScope);
      if (tok.type == TokenType.INFO) {
        assoc.meta.add(_parseMeta(parentScope, id));
      } else {
        assocs.add(assoc);
        assoc = _parseAssociation(parentScope, id);
      }
    }
    assocs.add(assoc);
    return assocs;
  }

  // <association> ::= <type> <mult> LARROW ID RARROW <mult> <type> ID
  private AST.Association _parseAssociation(ParserScope parentScope, AST.ID leftAsset) {
    ParserScope scope = new ParserScope(TokenSet.ASSOCIATION, parentScope);
    var leftField = _parseType(scope);
    var leftMult = _parseMultiplicity(scope);
    eat(TokenType.LARROW, scope);
    var linkName = _parseID(scope);
    eat(TokenType.RARROW, scope);
    var rightMult = _parseMultiplicity(scope);
    var rightField = _parseType(scope);
    var rightAsset = _parseID(scope);
    return new AST.Association(leftAsset.getLocation(), leftAsset, leftField, leftMult, linkName, rightMult, rightField,
        rightAsset, new ArrayList<>());
  }

  // <mult> ::= <mult-unit> (RANGE <mult-unit>)?
  private AST.Multiplicity _parseMultiplicity(ParserScope parentScope) {
    var min = _parseMultiplicityUnit(parentScope);
    if (tok.type == TokenType.RANGE) {
      _next();
      var max = _parseMultiplicityUnit(parentScope);
      if (min == 0 && max == 1) {
        return AST.Multiplicity.ZERO_OR_ONE;
      } else if (min == 0 && max == 2) {
        return AST.Multiplicity.ZERO_OR_MORE;
      } else if (min == 1 && max == 1) {
        return AST.Multiplicity.ONE;
      } else if (min == 1 && max == 2) {
        return AST.Multiplicity.ONE_OR_MORE;
      } else {
        return AST.Multiplicity.INVALID;
      }
    } else {
      if (min == 0) {
        return AST.Multiplicity.INVALID;
      } else if (min == 1) {
        return AST.Multiplicity.ONE;
      } else {
        return AST.Multiplicity.ZERO_OR_MORE;
      }
    }
  }

  private static char intToMult(int n) {
    switch (n) {
      case 0:
        return '0';
      case 1:
        return '1';
      default:
        return '*';
    }
  }

  // <mult-unit> ::= INT | STAR
  // 0 | 1 | *
  private int _parseMultiplicityUnit(ParserScope parentScope) {
    ParserScope scope = new ParserScope(TokenSet.MULTIPLICITY, parentScope);
    var token = eatEitherFromScope(scope, "a mulitplicity expression");
    if (token.type == TokenType.INT) {
      var n = token.intValue;
      if (n == 0 || n == 1) {
        _next();
        return n;
      } else {
        return 3;
      }
    } else if (token.type == TokenType.STAR) {
      _next();
      return 2;
    }
    return 3;
  }

  // <type> ::= LBRACKET ID RBRACKET
  private AST.ID _parseType(ParserScope parentScope) {
    eat(TokenType.LBRACKET, parentScope);
    var id = _parseID(parentScope);
    eat(TokenType.RBRACKET, parentScope);
    return id;
  }

}
