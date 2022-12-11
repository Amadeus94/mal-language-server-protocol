package org.mal.ls.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

/**
 * This class represents the completion item category
 */
public class CategoryItem extends CompletionItemMal {
    private static final String text = "category ";
    private static final String label = "category";
    private static final String info = "Similar to a package in Java. A category consists of one or more assets. The category does not bear semantics, it is only there to enable structure for the language developer.";
    private static final CompletionItemKind kind = CompletionItemKind.Keyword;

    public CategoryItem() {
        super(text, label, info, kind);
    }
}