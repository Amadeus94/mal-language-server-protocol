/** 
 * This class represents the completion item user info
 */
package org.mal.ls.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

public class UserInfo extends CompletionItemSnippetMal {
    private static final String text = "user info: \"${0}\"";
    private static final String label = "user info";
    private static final CompletionItemKind kind = CompletionItemKind.Snippet;
    private static final InsertTextFormat textFormat = InsertTextFormat.Snippet;

    public UserInfo() {
        super(text, label, kind, textFormat);
    }
}