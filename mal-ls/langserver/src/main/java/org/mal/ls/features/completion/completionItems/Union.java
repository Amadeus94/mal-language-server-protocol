/** 
 * This class represents the completion item union
 */
package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class Union extends CompletionItemMal {
    private static final String text = "\\/ ";
    private static final String label = "union";
    private static final String info = "Designates the union of the sets of associated assets designated by the roles foo and bar.";
    private static final CompletionItemKind kind = CompletionItemKind.Operator;

    public Union() {
        super(text, label, info, kind);
    }
}