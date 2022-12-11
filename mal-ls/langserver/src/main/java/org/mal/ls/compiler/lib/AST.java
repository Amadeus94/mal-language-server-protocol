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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class AST extends MalLocation {
  private List<Category> categories = new ArrayList<>();
  private List<Association> associations = new ArrayList<>();
  private List<Define> defines = new ArrayList<>();

  public AST(String uri) {
    super(uri, new Range(new Position(0, 0), new Position(0, 0)));
  }

  public void include(AST other) {
    this.categories.addAll(other.categories);
    this.associations.addAll(other.associations);
    this.defines.addAll(other.defines);
  }

  public List<Category> getCategories() {
    var categories = new ArrayList<Category>();
    categories.addAll(this.categories);
    return categories;
  }

  public void addCategory(Category category) {
    this.categories.add(category);
  }

  public List<Association> getAssociations() {
    var associations = new ArrayList<Association>();
    associations.addAll(this.associations);
    return associations;
  }

  public void addAssociations(List<Association> associations) {
    this.associations.addAll(associations);
  }

  public List<Define> getDefines() {
    var defines = new ArrayList<Define>();
    defines.addAll(this.defines);
    return defines;
  }

  public void addDefine(Define define) {
    this.defines.add(define);
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    sb.append(String.format("%s,%n", Define.listToString(defines, 0)));
    sb.append(String.format("%s,%n", Category.listToString(categories, 0)));
    sb.append(String.format("%s%n", Association.listToString(associations, 0)));
    return sb.toString();
  }

  public static class ID extends MalLocation {
    public final String id;

    public ID(MalLocation location, String id) {
      super(location);
      this.id = id;
    }

    public ID(Token token) {
      super(token);
      this.id = token.stringValue;
    }

    @Override
    public String toString() {
      if (id.equals("")) {
        return "ID: missing";
      } else {
        return String.format("ID: \"%s\", (%s)", id, locationString());
      }
    }
  }

  public static class Define extends MalLocation {
    public final ID key;
    public final String value;

    public Define(MalLocation firstLocation, ID key, Token string) {
      super(new MalLocation(firstLocation.getUri(), firstLocation.getStart(), string.getEnd()));
      this.key = key;
      this.value = string.stringValue;
    }

    public static String listToString(List<Define> defines, int spaces) {
      var indent = " ".repeat(spaces);
      var sb = new StringBuilder();
      sb.append(String.format("%sdefines = [%n", indent));
      for (int i = 0; i < defines.size(); i++) {
        sb.append(defines.get(i).toString(spaces + 1));
        if (i < defines.size() - 1) {
          sb.append(',');
        }
        sb.append('\n');
      }
      sb.append(String.format("%s]", indent));
      return sb.toString();
    }

    public String toString(int spaces) {
      String indent = " ".repeat(spaces);
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("%sdefine{%n", indent));
      sb.append(String.format("%s %s%n", indent, key.toString()));
      if (value.equals("")) {
        sb.append(String.format("%sstring: missing%n", indent));
      } else {
        sb.append(String.format("%sstring: \"%s\"%n", indent, value));
      }
      sb.append(String.format("%s}", indent));
      return sb.toString();
    }
  }

  public static class Meta extends MalLocation {
    public final ID type;
    public final String string;

    public Meta(MalLocation firstLocation, ID type, Token string) {
      super(new MalLocation(firstLocation.getUri(), firstLocation.getStart(), string.getEnd()));
      this.type = type;
      this.string = string.stringValue;
    }

    public String toString(int spaces) {
      String indent = " ".repeat(spaces);
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("%smeta{%n", indent));
      if (string.equals("")) {
        sb.append(String.format("%sstring: missing%n", indent));
      } else {
        sb.append(String.format("%sstring: \"%s\"%n", indent, string));
      }
      sb.append(String.format("%s}", indent));
      return sb.toString();
    }

    public static String listToString(List<Meta> meta, int spaces) {
      var indent = " ".repeat(spaces);
      var sb = new StringBuilder();
      sb.append(String.format("%smetas = [%n", indent));
      for (int i = 0; i < meta.size(); i++) {
        sb.append(String.format("%s", meta.get(i).toString(spaces + 1)));
        if (i < meta.size() - 1) {
          sb.append(',');
        }
        sb.append('\n');
      }
      sb.append(String.format("%s]", indent));
      return sb.toString();
    }

  }

  public static class Category extends MalLocation {
    public final ID name;
    public final List<Meta> meta;
    public final List<Asset> assets;

    public Category(MalLocation location, ID name, List<Meta> meta, List<Asset> assets) {
      super(location);
      this.name = name;
      this.meta = meta;
      this.assets = assets;
    }

    public List<Asset> getAssets() {
      List<Asset> assets = new ArrayList<>();
      assets.addAll(this.assets);
      return assets;
    }

    public String toString(int spaces) {
      String indent = " ".repeat(spaces);
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("%scategory{%n", indent));
      sb.append(String.format("%s %s%n", indent, name.toString()));
      sb.append(String.format("%s%n", Meta.listToString(meta, spaces + 1)));
      sb.append(String.format("%s%n", Asset.listToString(assets, spaces + 1)));
      sb.append(String.format("%s}", indent));
      return sb.toString();
    }

    public static String listToString(List<Category> categories, int spaces) {
      var indent = " ".repeat(spaces);
      var sb = new StringBuilder();
      sb.append(String.format("%scategories = [%n", indent));
      for (int i = 0; i < categories.size(); i++) {
        sb.append(String.format("%s", categories.get(i).toString(spaces + 1)));
        if (i < categories.size() - 1) {
          sb.append(',');
        }
        sb.append('\n');
      }
      sb.append(String.format("%s]%n", indent));
      return sb.toString();
    }

  }

  public static class Asset extends MalLocation {
    public final boolean isAbstract;
    public final ID name;
    public final Optional<ID> parent;
    public final List<Meta> meta;
    public final List<AttackStep> attackSteps;
    public final List<Variable> variables;

    public Asset(MalLocation pos, boolean isAbstract, ID name, Optional<ID> parent, List<Meta> meta,
        List<AttackStep> attackSteps, List<Variable> variables) {
      super(pos);
      this.isAbstract = isAbstract;
      this.name = name;
      this.parent = parent;
      this.meta = meta;
      this.attackSteps = attackSteps;
      this.variables = variables;
    }

    public List<Variable> getVariables() {
      return this.variables;
    }

    public ID getName() {
      return this.name;
    }

    public List<AttackStep> getAttacksteps() {
      return this.attackSteps;
    }

    public String toString(int spaces) {
      String indent = " ".repeat(spaces);
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("%s{%n", indent));
      sb.append(String.format("%s abstract: %s%n", indent, isAbstract));
      if (parent.isPresent()) {
        sb.append(String.format("%s parent: {%s}%n", indent, parent.get()));
      } else {
        sb.append(String.format("%s parent: {}%n", indent));
      }
      sb.append(String.format("%s%n", Meta.listToString(meta, spaces + 1)));
      sb.append(String.format("%s%n", Variable.listToString(variables, spaces + 1)));
      sb.append(String.format("%s%n", AttackStep.listToString(attackSteps, spaces + 1)));
      sb.append(String.format("%s}", indent));
      return sb.toString();
    }

    public static String listToString(List<Asset> assets, int spaces) {
      var indent = " ".repeat(spaces);
      var sb = new StringBuilder();
      sb.append(String.format("%sAssets = [%n", indent));
      for (int i = 0; i < assets.size(); i++) {
        sb.append(String.format("%s", assets.get(i).toString(spaces + 1)));
        if (i < assets.size() - 1) {
          sb.append(',');
        }
        sb.append('\n');
      }
      sb.append(String.format("%s]", indent));
      return sb.toString();
    }
  }

  public enum AttackStepType {
    ALL, ANY, DEFENSE, EXIST, NOTEXIST
  }

  public static class AttackStep extends MalLocation {
    public final AttackStepType type;
    public final ID name;
    public final List<ID> tags;
    public final Optional<List<CIA>> cia;
    public final Optional<TTCExpr> ttc;
    public final List<Meta> meta;
    public final Optional<Requires> requires;
    public final Optional<Reaches> reaches;

    public AttackStep(MalLocation location, AttackStepType type, ID name, List<ID> tags, Optional<List<CIA>> cia,
        Optional<TTCExpr> ttc, List<Meta> meta, Optional<Requires> requires, Optional<Reaches> reaches) {
      super(location);
      this.type = type;
      this.name = name;
      this.tags = tags;
      this.cia = cia;
      this.ttc = ttc;
      this.meta = meta;
      this.requires = requires;
      this.reaches = reaches;
    }

    public ID getName() {
      return this.name;
    }

    public Optional<Requires> getRequires() {
      return this.requires;
    }

    public Optional<Reaches> getReaches() {
      return this.reaches;
    }

    public String toString(int spaces) {
      var indent = " ".repeat(spaces);
      var sb = new StringBuilder();
      sb.append(String.format("%sAttackStep(%s, %s, %s,%n", indent, locationString(), type.name(), name.toString()));
      sb.append(String.format("%s  tags = {", indent));
      for (int i = 0; i < tags.size(); i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append(tags.get(i).toString());
      }
      sb.append(String.format("},%n"));
      if (cia.isEmpty()) {
        sb.append(String.format("%s  cia = {},%n", indent));
      } else {
        sb.append(String.format("%s  cia = {%s},%n", indent, CIA.listToString(cia.get())));
      }
      if (ttc.isEmpty()) {
        sb.append(String.format("%s  ttc = [],%n", indent));
      } else {
        sb.append(String.format("%s  ttc = [%s],%n", indent, ttc.get().toString()));
      }
      sb.append(String.format("%s,%n", Meta.listToString(meta, spaces + 2)));
      if (requires.isEmpty()) {
        sb.append(String.format("%s  NO_REQUIRES,%n", indent));
      } else {
        sb.append(String.format("%s,%n", requires.get().toString(spaces + 2)));
      }
      if (reaches.isEmpty()) {
        sb.append(String.format("%s  NO_REACHES%n", indent));
      } else {
        sb.append(String.format("%s%n", reaches.get().toString(spaces + 2)));
      }
      sb.append(String.format("%s)", indent));
      return sb.toString();
    }

    public static String listToString(List<AttackStep> attackSteps, int spaces) {
      var indent = " ".repeat(spaces);
      var sb = new StringBuilder();
      sb.append(String.format("%sattacksteps = [%n", indent));
      for (int i = 0; i < attackSteps.size(); i++) {
        sb.append(String.format("%s", attackSteps.get(i).toString(spaces + 2)));
        if (i < attackSteps.size() - 1) {
          sb.append(',');
        }
        sb.append(String.format("%n"));
      }
      sb.append(String.format("%s]", indent));
      return sb.toString();
    }
  }

  public enum CIA {
    C, I, A;

    public static String listToString(List<CIA> cia) {
      var sb = new StringBuilder();
      for (int i = 0; i < cia.size(); i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append(cia.get(i));
      }
      return sb.toString();
    }
  }

  public abstract static class TTCExpr extends MalLocation {
    public TTCExpr(MalLocation location) {
      super(location);
    }
  }

  public abstract static class TTCBinaryExpr extends TTCExpr {
    public final TTCExpr lhs;
    public final TTCExpr rhs;

    public TTCBinaryExpr(MalLocation location, TTCExpr lhs, TTCExpr rhs) {
      super(location);
      this.lhs = lhs;
      this.rhs = rhs;
    }
  }

  public static class TTCAddExpr extends TTCBinaryExpr {
    public TTCAddExpr(MalLocation location, TTCExpr lhs, TTCExpr rhs) {
      super(location, lhs, rhs);
    }

    @Override
    public String toString() {
      return String.format("TTCAddExpr(%s, %s, %s)", locationString(), lhs.toString(), rhs.toString());
    }
  }

  public static class TTCSubExpr extends TTCBinaryExpr {
    public TTCSubExpr(MalLocation location, TTCExpr lhs, TTCExpr rhs) {
      super(location, lhs, rhs);
    }

    @Override
    public String toString() {
      return String.format("TTCSubExpr(%s, %s, %s)", locationString(), lhs.toString(), rhs.toString());
    }
  }

  public static class TTCMulExpr extends TTCBinaryExpr {
    public TTCMulExpr(MalLocation location, TTCExpr lhs, TTCExpr rhs) {
      super(location, lhs, rhs);
    }

    @Override
    public String toString() {
      return String.format("TTCMulExpr(%s, %s, %s)", locationString(), lhs.toString(), rhs.toString());
    }
  }

  public static class TTCDivExpr extends TTCBinaryExpr {
    public TTCDivExpr(MalLocation location, TTCExpr lhs, TTCExpr rhs) {
      super(location, lhs, rhs);
    }

    @Override
    public String toString() {
      return String.format("TTCDivExpr(%s, %s, %s)", locationString(), lhs.toString(), rhs.toString());
    }
  }

  public static class TTCPowExpr extends TTCBinaryExpr {
    public TTCPowExpr(MalLocation location, TTCExpr lhs, TTCExpr rhs) {
      super(location, lhs, rhs);
    }

    @Override
    public String toString() {
      return String.format("TTCPowExpr(%s, %s, %s)", locationString(), lhs.toString(), rhs.toString());
    }
  }

  public static class TTCFuncExpr extends TTCExpr {
    public final ID name;
    public final List<Double> params;

    public TTCFuncExpr(MalLocation location, ID name, List<Double> params) {
      super(location);
      this.name = name;
      this.params = params;
    }

    @Override
    public String toString() {
      var sb = new StringBuilder();
      sb.append(String.format("TTCFuncExpr(%s, %s", locationString(), name.toString()));
      for (var p : params) {
        sb.append(String.format(", %f", p));
      }
      sb.append(')');
      return sb.toString();
    }
  }

  public static class TTCNumExpr extends TTCExpr {
    public final double value;

    public TTCNumExpr(MalLocation location, double value) {
      super(location);
      this.value = value;
    }

    @Override
    public String toString() {
      return String.format("TTCNumExpr(%s, %f)", locationString(), value);
    }
  }

  public static class Requires extends MalLocation {
    public final List<Expr> requires;

    public Requires(MalLocation location, List<Expr> requires) {
      super(location);
      this.requires = requires;
    }

    public String toString(int spaces) {
      var indent = " ".repeat(spaces);
      var sb = new StringBuilder();
      sb.append(String.format("%sRequires(%s,%n", indent, locationString()));
      sb.append(String.format("%s%n", Expr.listToString(requires, "requires", spaces + 2)));
      sb.append(String.format("%s)", indent));
      return sb.toString();
    }
  }

  public static class Reaches extends MalLocation {
    public final boolean inherits;
    public final List<Expr> reaches;

    public Reaches(MalLocation location, boolean inherits, List<Expr> reaches) {
      super(location);
      this.inherits = inherits;
      this.reaches = reaches;
    }

    public String toString(int spaces) {
      var indent = " ".repeat(spaces);
      var sb = new StringBuilder();
      sb.append(String.format("%sReaches(%s, %s,%n", indent, locationString(), inherits ? "INHERITS" : "OVERRIDES"));
      sb.append(String.format("%s%n", Expr.listToString(reaches, "reaches", spaces + 2)));
      sb.append(String.format("%s)", indent));
      return sb.toString();
    }
  }

  public static class Variable extends MalLocation {
    public final ID name;
    public final Expr expr;

    public Variable(MalLocation location, ID name, Expr expr) {
      super(location);
      this.name = name;
      this.expr = expr;
    }

    public String toString(int spaces) {
      String indent = " ".repeat(spaces);
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("%svariable{%n", indent));
      sb.append(String.format("%s %s%n", indent, expr));
      sb.append(String.format("%s}", indent));
      return sb.toString();
    }

    public static String listToString(List<Variable> variables, int spaces) {
      var indent = " ".repeat(spaces);
      var sb = new StringBuilder();
      sb.append(String.format("%svariables = [%n", indent));
      for (int i = 0; i < variables.size(); i++) {
        sb.append(String.format("%s", variables.get(i).toString(spaces + 1)));
        if (i < variables.size() - 1) {
          sb.append(',');
        }
        sb.append('\n');
      }
      sb.append(String.format("%s]", indent));
      return sb.toString();
    }
  }

  public abstract static class Expr extends MalLocation {
    public Expr(MalLocation location) {
      super(location);
    }

    public static String listToString(List<Expr> exprs, String name, int spaces) {
      var indent = " ".repeat(spaces);
      var sb = new StringBuilder();
      sb.append(String.format("%s%s = {%n", indent, name));
      for (int i = 0; i < exprs.size(); i++) {
        sb.append(String.format("%s  %s", indent, exprs.get(i).toString()));
        if (i < exprs.size() - 1) {
          sb.append(',');
        }
        sb.append(String.format("%n"));
      }
      sb.append(String.format("%s}", indent));
      return sb.toString();
    }
  }

  public abstract static class BinaryExpr extends Expr {
    public Expr lhs;
    public Expr rhs;

    public BinaryExpr(MalLocation location, Expr lhs, Expr rhs) {
      super(location);
      this.lhs = lhs;
      this.rhs = rhs;
    }
  }

  public static class UnionExpr extends BinaryExpr {
    public UnionExpr(MalLocation location, Expr lhs, Expr rhs) {
      super(location, lhs, rhs);
    }

    @Override
    public String toString() {
      return String.format("UnionExpr(%s, %s, %s)", locationString(), lhs.toString(), rhs.toString());
    }
  }

  public static class DifferenceExpr extends BinaryExpr {
    public DifferenceExpr(MalLocation location, Expr lhs, Expr rhs) {
      super(location, lhs, rhs);
    }

    @Override
    public String toString() {
      return String.format("DifferenceExpr(%s, %s, %s)", locationString(), lhs.toString(), rhs.toString());
    }
  }

  public static class IntersectionExpr extends BinaryExpr {
    public IntersectionExpr(MalLocation location, Expr lhs, Expr rhs) {
      super(location, lhs, rhs);
    }

    @Override
    public String toString() {
      return String.format("IntersectionExpr(%s, %s, %s)", locationString(), lhs.toString(), rhs.toString());
    }
  }

  public static class StepExpr extends BinaryExpr {
    public StepExpr(MalLocation location, Expr lhs, Expr rhs) {
      super(location, lhs, rhs);
    }

    @Override
    public String toString() {
      return String.format("StepExpr(%s, %s, %s)", locationString(), lhs.toString(), rhs.toString());
    }
  }

  public abstract static class UnaryExpr extends Expr {
    public final Expr e;

    public UnaryExpr(MalLocation location, Expr e) {
      super(location);
      this.e = e;
    }
  }

  public static class ParenExpr extends UnaryExpr {
    public final Token lparen;
    public final Token rparen;

    public ParenExpr(MalLocation location, Expr e, Token lparen, Token rparen) {
      super(location, e);
      this.lparen = lparen;
      this.rparen = rparen;
    }

    @Override
    public String toString() {
      return String.format("ParenExpr(%s, %s)", locationString(), this.e);
    }
  }

  public static class TransitiveExpr extends UnaryExpr {
    public TransitiveExpr(MalLocation location, Expr e) {
      super(location, e);
    }

    @Override
    public String toString() {
      return String.format("TransitiveExpr(%s, %s)", locationString(), e.toString());
    }
  }

  public static class SubTypeExpr extends UnaryExpr {
    public final ID subType;

    public SubTypeExpr(MalLocation location, Expr e, ID subType) {
      super(location, e);
      this.subType = subType;
    }

    @Override
    public String toString() {
      return String.format("SubTypeExpr(%s, %s, %s)", locationString(), e.toString(), subType.toString());
    }
  }

  public static class IDExpr extends Expr {
    public final ID id;

    public IDExpr(MalLocation location, ID id) {
      super(location);
      this.id = id;
    }

    @Override
    public String toString() {
      return String.format("IDExpr(%s, %s)", locationString(), id.toString());
    }
  }

  public static class CallExpr extends Expr {
    public final ID id;

    public CallExpr(MalLocation location, ID id) {
      super(location);
      this.id = id;
    }

    @Override
    public String toString() {
      return String.format("CallExpr(%s, %s)", locationString(), id.toString());
    }
  }

  public static class Association extends MalLocation {
    public final ID leftAsset;
    public final ID leftField;
    public final Multiplicity leftMult;
    public final ID linkName;
    public final Multiplicity rightMult;
    public final ID rightField;
    public final ID rightAsset;
    public final List<Meta> meta;

    public Association(MalLocation location, ID leftAsset, ID leftField, Multiplicity leftMult, ID linkName,
        Multiplicity rightMult, ID rightField, ID rightAsset, List<Meta> meta) {
      super(location);
      this.leftAsset = leftAsset;
      this.leftField = leftField;
      this.leftMult = leftMult;
      this.linkName = linkName;
      this.rightMult = rightMult;
      this.rightField = rightField;
      this.rightAsset = rightAsset;
      this.meta = meta;
    }

    public String toString(int spaces) {
      var indent = " ".repeat(spaces);
      var sb = new StringBuilder();
      sb.append(String.format("%sAssociation(%s, %s, %s, %s, %s, %s, %s, %s,%n", indent, locationString(),
          leftAsset.toString(), leftField.toString(), leftMult.name(), linkName.toString(), rightMult.name(),
          rightField.toString(), rightAsset.toString()));
      sb.append(String.format("%s%n", Meta.listToString(meta, spaces + 2)));
      sb.append(String.format("%s)", indent));
      return sb.toString();
    }

    public String toShortString() {
      return String.format("%s [%s] <-- %s --> %s [%s]", leftAsset.id, leftField.id, linkName.id, rightAsset.id,
          rightField.id);
    }

    public static String listToString(List<Association> associations, int spaces) {
      var indent = " ".repeat(spaces);
      var sb = new StringBuilder();
      sb.append(String.format("%sassociations = {%n", indent));
      for (int i = 0; i < associations.size(); i++) {
        sb.append(String.format("%s", associations.get(i).toString(spaces + 2)));
        if (i < associations.size() - 1) {
          sb.append(',');
        }
        sb.append(String.format("%n"));
      }
      sb.append(String.format("%s}", indent));
      return sb.toString();
    }
  }

  public enum Multiplicity {
    ZERO_OR_ONE("0..1"), ZERO_OR_MORE("*"), ONE("1"), ONE_OR_MORE("1..*"), INVALID("invalid");

    private String string;

    private Multiplicity(String string) {
      this.string = string;
    }

    @Override
    public String toString() {
      return string;
    }
  }
}
