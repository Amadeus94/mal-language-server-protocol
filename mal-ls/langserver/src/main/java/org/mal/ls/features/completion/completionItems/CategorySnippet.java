package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

public class CategorySnippet extends CompletionItemSnippetMal {
  private static final String text = "category ${1:name} {\n\t${2}\n}\n";
  private static final String label = "category-snippet";
  private static final CompletionItemKind kind = CompletionItemKind.Snippet;
  private static final InsertTextFormat textFormat = InsertTextFormat.Snippet;

  public CategorySnippet() {
    super(text, label, kind, textFormat);
  }
}