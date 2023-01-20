package org.mal.ls.features.completion.completionItems;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;

public class CompletionItemMal {

  public CompletionItem ci;

  public CompletionItemMal(String text, String label, String info, CompletionItemKind kind) {
    this.ci = new CompletionItem();
    this.ci.setInsertText(text);
    this.ci.setLabel(label);
    this.ci.setKind(kind);
    this.ci.setDetail(info);
  }
}