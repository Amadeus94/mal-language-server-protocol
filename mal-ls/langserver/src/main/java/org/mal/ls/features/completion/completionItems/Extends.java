/** 
 * This class represents the completion item extends
 */
package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class Extends extends CompletionItemMal {
    private static final String text = "extends ";
    private static final String label = "extends";
    private static final String info = "The child asset is a sub-class of Parent, the extended class. Child additionally inherits the logic specified for its parent.";
    private static final CompletionItemKind kind = CompletionItemKind.Keyword;

    public Extends() {
        super(text, label, info, kind);
    }
}