package org.mal.ls.features.symbol;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.mal.ls.compiler.lib.AST;
import org.mal.ls.compiler.lib.AST.Asset;
import org.mal.ls.compiler.lib.AST.Association;
import org.mal.ls.compiler.lib.AST.AttackStep;
import org.mal.ls.compiler.lib.AST.BinaryExpr;
import org.mal.ls.compiler.lib.AST.CallExpr;
import org.mal.ls.compiler.lib.AST.Category;
import org.mal.ls.compiler.lib.AST.Expr;
import org.mal.ls.compiler.lib.AST.IDExpr;
import org.mal.ls.compiler.lib.AST.Reaches;
import org.mal.ls.compiler.lib.AST.Requires;
import org.mal.ls.compiler.lib.AST.Variable;

public class MALSymbolProvider {

  private String uri = "";
  private String variable = "";
  private int cursorLine = 0;
  private int cursorChar = 0;
  private AST ast;
  private List<Location> locations;
  // private DefinitionContext dc;

  public List<Symbol> getSymbols(AST ast) {
    // 1. Make a flat structure of all symbols. This means I will add everything
    // into same list
    List<Symbol> symbols = new ArrayList<>();

    // 2. Iterate over all categories
    ast.getCategories().forEach((category) -> {
      symbols.add(new Symbol(category.name.id, "category", category.name.getLocation()));
      category.getAssets().forEach((asset) -> {

        symbols.add(new Symbol(asset.name.id, "asset", asset.name.getLocation()));
        asset.getAttacksteps().forEach((attackStep) -> {
          symbols.add(new Symbol(attackStep.name.id, "attackStep", attackStep.name.getLocation()));
        });
        asset.getVariables().forEach((variable) -> {
          symbols.add(new Symbol(variable.name.id, "variable", variable.name.getLocation()));
        });
      });
    });

    //TODO: Associations - Maybe change to ID getlocation?
    ast.getAssociations().forEach((association) -> {
      symbols.add(new Symbol(association.leftAsset.id,  "association",   association.leftAsset.getLocation()));
      symbols.add(new Symbol(association.linkName.id,   "linkName",       association.linkName.getLocation()));
      symbols.add(new Symbol(association.rightAsset.id, "association",  association.rightAsset.getLocation()));
      symbols.add(new Symbol(association.rightField.id, "field",  association.rightField.getLocation()));
      symbols.add(new Symbol(association.leftField.id, "field",  association.leftField.getLocation()));
    });;

    //TODO: 
    ast.getDefines();

    return symbols;
  }

  private void reset() {
    this.variable = "";
    this.cursorLine = 0;
    this.cursorChar = 0;
  }

  private String setLowerCase(String str) {
    char c[] = str.toCharArray();
    c[0] = Character.toLowerCase(c[0]);
    return new String(c);
  }

  private String getDefinitionUri(String fileName) {
    StringBuilder sb = new StringBuilder();
    String[] path = this.uri.split("/");
    for (int i = 0; i < path.length - 1; i++) {
      sb.append(path[i]);
      sb.append("/");
    }
    sb.append(fileName);
    return sb.toString();
  }

  /*
   * Finds the range to which corresponds to the earlier found variable
   */
  public List<Location> getDefinitionLocations(String uri) {
    this.uri = uri;
    this.locations = new ArrayList<>();
    return this.locations;
  }


  /*
   * Finds and sets the token name to the corresponding postion
   */
  public String getVariable(Position position, AST ast) {
    reset();
    this.ast = ast;
    this.cursorLine = position.getLine();
    this.cursorChar = position.getCharacter();

    iterAssociation(ast.getAssociations());
    if (this.variable.equals(""))
      iterAST(ast.getCategories());
    return this.variable;
  }

  private void iterAssociation(List<Association> associations) {
    int line = this.cursorLine;
    int character = this.cursorChar;
    associations.forEach((association) -> {
      if (association.getEnd().getLine() >= line && line >= association.getStart().getLine()) {
        if (association.leftAsset.getEnd().getCharacter() >= character
            && character >= association.leftAsset.getStart().getCharacter()) {
          this.variable = setLowerCase(association.leftAsset.id);
        } else if (association.rightAsset.getEnd().getCharacter() >= character
            && character >= association.rightAsset.getStart().getCharacter()) {
          this.variable = setLowerCase(association.rightAsset.id);
        }
      }
    });
  }

  private void iterAST(List<Category> categories) {
    categories.forEach((category) -> {
      category.assets.forEach((asset) -> {
        asset.attackSteps.forEach((as) -> {
          if (!as.requires.isEmpty()) {
            Requires r = as.requires.orElse(null);
            iterExpr(r.requires);
          }
          if (!as.reaches.isEmpty()) {
            Reaches r = as.reaches.orElse(null);
            iterExpr(r.reaches);
          }
        });
      });
    });
  }

  private void iterExpr(List<Expr> e) {
    e.forEach((expr) -> {
      checkExpr(expr);
    });
  }

  private void checkExpr(Expr expr) {
    int line = this.cursorLine;
    int character = this.cursorChar;

    if (expr.getEnd().getLine() >= line && line >= expr.getStart().getLine()) {
      if (expr instanceof BinaryExpr) {
        BinaryExpr binaryExpr = (BinaryExpr) expr;
        checkExpr(binaryExpr.lhs);
        checkExpr(binaryExpr.rhs);
      } else if (expr instanceof CallExpr) {
        CallExpr callExpr = (CallExpr) expr;
        if (callExpr.getEnd().getCharacter() >= character && character >= callExpr.getStart().getCharacter()) {
          this.variable = setLowerCase(callExpr.id.id);
        }
      } else {
        IDExpr idExpr = (IDExpr) expr;
        if (idExpr.getEnd().getCharacter() >= character && character >= idExpr.getStart().getCharacter()) {
          this.variable = setLowerCase(idExpr.id.id);
        }
      }
    }
  }
}