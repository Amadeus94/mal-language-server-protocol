/** 
 * This class represents the completion item existence
 */
package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class Existence extends CompletionItemMal {
    private static final String text = "E ";
    private static final String label = "existence";
    private static final String info = "It is used when the exsistence of connected/associated assets, given by <-, must be checked. The specified attack steps are reachable when at least one designated asset exist.";
    private static final CompletionItemKind kind = CompletionItemKind.Operator;

    public Existence() {
        super(text, label, info, kind);
    }
}