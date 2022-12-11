/** 
 * This class represents the completion item OR
 */
package org.mal.ls.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class OR extends CompletionItemMal {
    private static final String text = "| ";
    private static final String label = "OR";
    private static final String info = "An OR attack step A can be reached if any of the attack steps which refers to A is reached.";
    private static final CompletionItemKind kind = CompletionItemKind.Operator;

    public OR() {
        super(text, label, info, kind);
    }
}