package org.mal.ls.features.hover;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.lsp4j.Range;
import org.mal.ls.compiler.lib.AST;

public class HoverProvider {

    /**
     * Category (ID name) Meta (ID type) Asset(ID name) Meta AttackStep (ID
     * name) Meta Variable ID name
     */
    static public HashMap<Range, String> fillItemsHashMap(AST ast) {
        HashMap<Range, String> map = new HashMap<>();

        // Categories:
        for (var i : ast.getCategories()) {
            map.put(i.getRange(), i.name.toString());
            for (var k : i.meta) {
                map.put(k.getRange(), k.type.toString());
            }
            for (var j : i.assets) {
                map.put(j.getRange(), j.name.toString());
                for (var ji : j.getAttacksteps()) {
                    map.put(ji.getRange(), ji.name.toString());
                }
                for (var ji : j.getVariables()) {
                    map.put(ji.getRange(), ji.name.toString());
                }
            }
        }

        for (var item : ast.getAssociations()) {
            map.put(item.getRange(), item.linkName.toString());
        }
        for (var item : ast.getDefines()) {
            map.put(item.getRange(), item.key.toString());
        }
        return map;
    }

    static public Set<HoverModel> fillItemsList(AST ast) {
        Set<HoverModel> list = new LinkedHashSet<>(); // unique Items

        // Categories: TODO: Use forEach instead

//        ast.getCategories().forEach((category) -> {
            //list.add(new HoverModel(category.getRange(), category.name.toString(), "category", "a **category** is similar to a package in Java. A category consists of one or more assets. The category does not bear semantics, it is only there to enable structure for the language developer."));
            //category.assets.forEach((asset)->{

            //});

        //});

        for (var i : ast.getCategories()) {
            list.add(new HoverModel(i.getRange(), i.name.toString(), "category", "a **category** is similar to a package in Java. A category consists of one or more assets. The category does not bear semantics, it is only there to enable structure for the language developer."));
            for (var k : i.meta) {
                list.add(new HoverModel(k.getRange(), k.type.toString(), "meta", "**meta** describes something about the category"));
            }
            for (var j : i.assets) {
                list.add(new HoverModel(j.getRange(), j.name.toString(), "asset", "When the MAL compiler generates the Java code from the MAL specifications, an **asset** is translated into a java class."));
                for (var ji : j.getAttacksteps()) {
                    list.add(new HoverModel(ji.getRange(), ji.name.toString(), "attackStep", "**attackstep** is describes the attackstep in the model"));
                }
                for (var ji : j.getVariables()) {
                    list.add(new HoverModel(ji.getRange(), ji.name.toString(), "variable", "**variable** is similar to a variable in java"));
                }
            }
        }

        for (var i : ast.getAssociations()) {
            list.add(new HoverModel(i.getRange(), i.linkName.toString(), "association", "Any number of Asset1 instantiations can be connected/ have an **association** to any number of Asset2 instantiations. Inline references from Asset1 to Asset2 use the name bar. Conversely, Asset2 refers to Asset1 with the name foo."));
        }
        for (var i : ast.getDefines()) {
            list.add(new HoverModel(i.getRange(), i.key.toString(), "define", "define"));
        }
        return list;
    }
}