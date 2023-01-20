/** 
 * This class represents the completion item leads to
 */
package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class LeadsTo extends CompletionItemMal {
    private static final String text = "-> ";
    private static final String label = "leads to";
    private static final String info = "The successful compromise of this attack step allows the attacker to consequently compromise further attack steps. Also specifies steps affected by defenses and existence checks.";
    private static final CompletionItemKind kind = CompletionItemKind.Operator;

    public LeadsTo() {
        super(text, label, info, kind);
    }
}