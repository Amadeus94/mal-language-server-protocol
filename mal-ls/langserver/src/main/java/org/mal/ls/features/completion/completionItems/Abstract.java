/** 
 * This class represents the completion item abstract
 */
package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class Abstract extends CompletionItemMal {
    private static final String text = "abstract ";
    private static final String label = "abstract";
    private static final String info = "When the MAL compiler generates the Java code from the MAL specifications, an abstract asset is translated into a abstract java class.";
    private static final CompletionItemKind kind = CompletionItemKind.Keyword;

    public Abstract() {
        super(text, label, info, kind);
    }
}