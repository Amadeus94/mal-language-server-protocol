/** 
 * This class represents the completion item append
 */
package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

public class Append extends CompletionItemMal {
    private static final String text = "+> ";
    private static final String label = "append";
    private static final String info = "Child assets only. When parent and child assets have overlapping elements, e.g. | access, the expressions defined for the child access are appended to those of the parent access. The child expressions will otherwise override those of the parent.";
    private static final CompletionItemKind kind = CompletionItemKind.Operator;

    public Append() {
        super(text, label, info, kind);
    }
}