package org.mal.ls.features.reference;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.mal.ls.compiler.lib.AST;
import org.mal.ls.compiler.lib.Analyzer;
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

public class ReferenceProvider {

    private String uri = "";
    private String variable = "";
    private int cursorLine = 0;
    private int cursorChar = 0;
    private AST ast;
    private List<Location> locations;

    private void reset() {
        this.variable = "";
        this.cursorLine = 0;
        this.cursorChar = 0;
    }

    private String setLowerCase(String str) {
        char c[] = str.toCharArray();
        c[0] = Character.toLowerCase(c[0]); // only sets first character to lower?
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

    // find the range to which corresponds to the earlier found variable
    public List<Location> getDefinitionLocations(String uri) {
        this.uri = uri;
        this.locations = new ArrayList<>();
        iterCategories(this.ast.getCategories());
        return this.locations;
    }

    private void iterCategories(List<Category> categories) {
        categories.forEach((category) -> {
            if (setLowerCase(category.name.id).equals(this.variable))
                locations.add(new Location(getDefinitionUri(category.getUri()), category.getRange()));
            iterAssets(category.assets);
        });
    }

    private void iterAssets(List<Asset> assets) {
        assets.forEach((asset) -> {
            if (setLowerCase(asset.name.id).equals(this.variable))
                locations.add(new Location(getDefinitionUri(asset.getUri()), asset.getRange()));
            iterVariables(asset.variables);
            iterAttackSteps(asset.attackSteps);
        });
    }

    private void iterVariables(List<Variable> variables) {
        variables.forEach((variable) -> {
            if (setLowerCase(variable.name.id).equals(this.variable))
                locations.add(new Location(getDefinitionUri(variable.getUri()), variable.getRange()));
        });
    }

    private void iterAttackSteps(List<AttackStep> attackSteps) {
        attackSteps.forEach((as) -> {
            if (setLowerCase(as.name.id).equals(this.variable))
                locations.add(new Location(getDefinitionUri(as.getUri()), as.getRange()));
        });
    }

    // find and set the token anme to the corresponding position
    public String getVariable(Position position, AST ast) {
        reset();
        this.ast = ast;
        this.cursorLine = position.getLine();
        this.cursorChar = position.getCharacter();

        iterAssociation(ast.getAssociations()); // try find variable in association?
        if (this.variable.equals("")) // if variable not found
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
    public static List<Location> references(
            AST ast,
            Position position) {
        List<Location> locations = new ArrayList<>();
        List<AST.Variable> variables = new ArrayList<>();

        // fill
        ast.getCategories().forEach((category) -> {
            category.assets.forEach((asset) -> {
                asset.attackSteps.forEach((attackStep) -> {
                });
                asset.variables.forEach((variable) -> {
                    variables.add(variable);
                });

            });
        });

        String variableName;

        // for(var variable: variables){
        // if(position == variable.posString())
        // variableName = "";
        // }

        return locations;
    }
}
