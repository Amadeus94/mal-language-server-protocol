package org.mal.ls.features.hover;

import java.util.ArrayList;
import java.util.List;
import org.mal.ls.compiler.lib.AST;

public class HoverProvider {

    static public List<HoverModel> fillItemsList(AST ast) {
        List<HoverModel> list = new ArrayList<>();

        ast.getCategories().forEach((category) -> {
            list.add(new HoverModel(category.name.getLocation().getRange(), category.name.id, "category",
                    "a **category** is similar to a package in Java. A category consists of one or more assets. The category does not bear semantics, it is only there to enable structure for the language developer."));
            category.assets.forEach((asset) -> {
                list.add(new HoverModel(asset.name.getLocation().getRange(), asset.name.id, "asset",
                        "When the MAL compiler generates the Java code from the MAL specifications, an **asset** is translated into a java class."));
                asset.attackSteps.forEach((attackStep) -> {
                    list.add(new HoverModel(attackStep.name.getLocation().getRange(), attackStep.name.id, "attackStep",
                            "**attackstep** is describes the attackstep in the model"));
                });
                asset.variables.forEach((variable) -> {
                    list.add(new HoverModel(variable.name.getLocation().getRange(), variable.name.id, "variable",
                            "**variable** is similar to a variable in java"));
                });
                asset.meta.forEach((meta) -> {
                    list.add(new HoverModel(meta.getLocation().getRange(), meta.type.id, "asset",
                            "When the MAL compiler generates the Java code from the MAL specifications, an **asset** is translated into a java class."));
                });
                category.meta.forEach((meta) -> {
                    list.add(new HoverModel(meta.type.getLocation().getRange(), meta.type.id, "asset",
                            "When the MAL compiler generates the Java code from the MAL specifications, an **asset** is translated into a java class."));
                });

            });

        });
        ast.getAssociations().forEach((association) -> {
            list.add(new HoverModel(association.getLocation().getRange(), association.linkName.toString(),
                    "association",
                    "Any number of Asset1 instantiations can be connected/ have an **association** to any number of Asset2 instantiations. Inline references from Asset1 to Asset2 use the name bar. Conversely, Asset2 refers to Asset1 with the name foo."));
        });
        ast.getDefines().forEach((define) -> {
            list.add(new HoverModel(define.getLocation().getRange(), define.key.toString(), "define", "define"));
        });
        return list;
    }
}