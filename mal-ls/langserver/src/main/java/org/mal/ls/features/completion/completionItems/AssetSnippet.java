package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

public class AssetSnippet extends CompletionItemSnippetMal {
  private static final String text = "asset ${1:name} {\n\t${2}\n}";
  private static final String label = "asset-snippet";
  private static final CompletionItemKind kind = CompletionItemKind.Snippet;
  private static final InsertTextFormat textFormat = InsertTextFormat.Snippet;

  public AssetSnippet() {
    super(text, label, kind, textFormat);
  }
}