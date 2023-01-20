/** 
 * This class represents the completion item non-existence
 */
package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class NonExistence extends CompletionItemMal {
    private static final String text = "!E ";
    private static final String label = "non-existence";
    private static final String info = "Same as Existence, except the specified attack steps are reachable when at least one designated asset does NOT exist.";
    private static final CompletionItemKind kind = CompletionItemKind.Operator;

    public NonExistence() {
        super(text, label, info, kind);
    }
}