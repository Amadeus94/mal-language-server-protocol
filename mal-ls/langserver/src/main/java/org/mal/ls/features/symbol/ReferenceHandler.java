package org.mal.ls.features.symbol;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.SymbolInformation;
import org.mal.ls.compiler.lib.AST;
import org.mal.ls.compiler.lib.Analyzer;

public class ReferenceHandler {
    static public List<Symbol> getSymbols(AST ast) {
        List<Symbol> list = new ArrayList<>(); // unique Items



        // 1. Parse the document using the AST -- Already done
        // 2. Iterate through the AST and add the symbols to the list
        // 3. Return the list

        ast.getCategories().forEach((category) -> {
            String name = category.name.id;
            String kind = "category";
            Location location = category.getLocation();
            //Symbol symbol = new Symbol (name, kind, new Location());

            //list.add(symbol);
        })
        
        
        
        
        
        ;

        return list;
    }

}
            /* 


            category.assets.forEach((asset) -> {
                list.add(new HoverModel(asset.getLocation().getRange(), asset.name.id, "asset", "When the MAL compiler generates the Java code from the MAL specifications, an **asset** is translated into a java class."));
                asset.attackSteps.forEach((attackStep) -> {
                    list.add(new HoverModel(attackStep.getLocation().getRange(), attackStep.name.id, "attackStep", "**attackstep** is describes the attackstep in the model"));
                });
                asset.variables.forEach((variable) -> {
                    list.add(new HoverModel(variable.getLocation().getRange(), variable.name.id, "variable", "**variable** is similar to a variable in java"));
                });
                asset.meta.forEach((meta) -> {
                    list.add(new HoverModel(meta.getLocation().getRange(), meta.type.id, "asset", "When the MAL compiler generates the Java code from the MAL specifications, an **asset** is translated into a java class."));
                });
            category.meta.forEach((meta) ->{
                list.add(new HoverModel(meta.type.getLocation().getRange(), meta.type.id, "asset", "When the MAL compiler generates the Java code from the MAL specifications, an **asset** is translated into a java class."));
            });

            });

        });
        ast.getAssociations().forEach((association) -> {
            list.add(new HoverModel(association.getLocation().getRange(), association.linkName.toString(), "association", "Any number of Asset1 instantiations can be connected/ have an **association** to any number of Asset2 instantiations. Inline references from Asset1 to Asset2 use the name bar. Conversely, Asset2 refers to Asset1 with the name foo."));
        });
        ast.getDefines().forEach((define) -> {
            list.add(new HoverModel(define.getLocation().getRange(), define.key.toString(), "define", "define"));
        });
        return list;
    }
*/
    
