/** 
 * This class represents the completion item let
 */
package org.mal.ls.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class Let extends CompletionItemMal {
    private static final String text = "let ";
    private static final String label = "let";
    private static final String info = "This keyword is used to associate a given expression with a specific reusable name.";
    private static final CompletionItemKind kind = CompletionItemKind.Keyword;

    public Let() {
        super(text, label, info, kind);
    }
}