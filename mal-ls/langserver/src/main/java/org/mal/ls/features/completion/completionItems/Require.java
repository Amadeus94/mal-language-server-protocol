/** 
 * This class represents the completion item require
 */
package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class Require extends CompletionItemMal {
    private static final String text = "<- ";
    private static final String label = "require";
    private static final String info = "Denotes which associated assets are required by the current expression. Used to specify preconditions for Existence and Non-existence.";
    private static final CompletionItemKind kind = CompletionItemKind.Operator;

    public Require() {
        super(text, label, info, kind);
    }
}