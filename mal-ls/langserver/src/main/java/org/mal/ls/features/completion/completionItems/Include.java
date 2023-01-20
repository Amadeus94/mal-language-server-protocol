/** 
 * This class represents the completion item include
 */
package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

public class Include extends CompletionItemSnippetMal {
    private static final String text = "include \"${0}\"";
    private static final String label = "include";
    private static final CompletionItemKind kind = CompletionItemKind.Snippet;
    private static final InsertTextFormat textFormat = InsertTextFormat.Snippet;

    public Include() {
        super(text, label, kind, textFormat);
    }
}