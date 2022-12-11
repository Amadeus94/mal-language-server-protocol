/** 
 * This class represents the completion item AND
 */
package org.mal.ls.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class AND extends CompletionItemMal {
    private static final String text = "& ";
    private static final String label = "AND";
    private static final String info = "An AND attack step A can be reached only if all of the attack steps which refer to A are reached.";
    private static final CompletionItemKind kind = CompletionItemKind.Operator;

    public AND() {
        super(text, label, info, kind);
    }
}