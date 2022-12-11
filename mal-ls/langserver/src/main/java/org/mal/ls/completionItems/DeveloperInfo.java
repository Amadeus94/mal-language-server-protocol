/** 
 * This class represents the completion item developer info
 */
package org.mal.ls.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

public class DeveloperInfo extends CompletionItemSnippetMal {
    private static final String text = "developer info: \"${0}\"";
    private static final String label = "developer info";
    private static final CompletionItemKind kind = CompletionItemKind.Snippet;
    private static final InsertTextFormat textFormat = InsertTextFormat.Snippet;

    public DeveloperInfo() {
        super(text, label, kind, textFormat);
    }
}