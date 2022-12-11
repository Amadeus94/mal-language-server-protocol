/** 
 * This class represents the completion item define
 */
package org.mal.ls.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

public class Define extends CompletionItemSnippetMal {
    private static final String text = "#${1:key}: \"${2:value}\"";
    private static final String label = "define";
    private static final CompletionItemKind kind = CompletionItemKind.Snippet;
    private static final InsertTextFormat textFormat = InsertTextFormat.Snippet;

    public Define() {
        super(text, label, kind, textFormat);
    }
}