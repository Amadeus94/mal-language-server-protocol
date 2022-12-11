/** 
 * This class represents the completion item intersection
 */
package org.mal.ls.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class Intersection extends CompletionItemMal {
    private static final String text = "/\\ ";
    private static final String label = "intersection";
    private static final String info = "Designates the intersection of the sets of associated assets designated by the roles foo and bar.";
    private static final CompletionItemKind kind = CompletionItemKind.Operator;

    public Intersection() {
        super(text, label, info, kind);
    }
}