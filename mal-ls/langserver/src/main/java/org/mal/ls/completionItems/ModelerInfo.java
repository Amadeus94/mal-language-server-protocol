/** 
 * This class represents the completion item modeler info
 */
package org.mal.ls.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

public class ModelerInfo extends CompletionItemSnippetMal {
    private static final String text = "modeler info: \"${0}\"";
    private static final String label = "modeler info";
    private static final CompletionItemKind kind = CompletionItemKind.Snippet;
    private static final InsertTextFormat textFormat = InsertTextFormat.Snippet;

    public ModelerInfo() {
        super(text, label, kind, textFormat);
    }
}