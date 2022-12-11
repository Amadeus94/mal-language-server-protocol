package org.mal.ls.completionItems;

import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;

public class AssociationSnippet extends CompletionItemSnippetMal {
  private static final String text = "associations {\n\t${1:Asset1} [${2:foo}] ${3:*} <-- ${4:connects} --> ${5:*} [${6:bar}] ${7:Asset2}\n}\n";
  private static final String label = "association-snippet";
  private static final CompletionItemKind kind = CompletionItemKind.Snippet;
  private static final InsertTextFormat textFormat = InsertTextFormat.Snippet;

  public AssociationSnippet() {
    super(text, label, kind, textFormat);
  }
}