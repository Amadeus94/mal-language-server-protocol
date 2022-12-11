package org.mal.ls.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

/**
 * This class represents the completion item associations
 */
public class AssociationItem extends CompletionItemMal {
    private static final String text = "associations";
    private static final String label = "associations";
    private static final String info = "Any number of Asset1 instantiations can be connected to any number of Asset2 instantiations. Inline references from Asset1 to Asset2 use the name bar. Conversely, Asset2 refers to Asset1 with the name foo.";
    private static final CompletionItemKind kind = CompletionItemKind.Keyword;

    public AssociationItem() {
        super(text, label, info, kind);
    }
}