package org.mal.ls.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;

/**
 * This class represents the completion item asset
 */
public class AssetItem extends CompletionItemMal {

    private static final String text = "asset ";
    private static final String label = "asset";
    private static final String info = "When the MAL compiler generates the Java code from the MAL specifications, an asset is translated into a java class.";
    private static final CompletionItemKind kind = CompletionItemKind.Keyword;

    public AssetItem() {
        super(text, label, info, kind);
    }
}