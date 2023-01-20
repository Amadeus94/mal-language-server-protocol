package org.mal.ls.features.diagnostics;

import java.util.List;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.mal.ls.compiler.lib.Analyzer;
import org.mal.ls.compiler.lib.MalDiagnosticLogger;
import org.mal.ls.context.ContextKeys;
import org.mal.ls.context.LanguageServerContext;

public class DiagnosticProvider {

  public void sendDiagnostics(LanguageClient client, LanguageServerContext context) {
    Analyzer.analyze(context.get(ContextKeys.AST_KEY));
    List<Diagnostic> diagnostics = List.copyOf(MalDiagnosticLogger.getInstance().messages);
    client.publishDiagnostics(new PublishDiagnosticsParams(context.get(ContextKeys.URI_KEY), diagnostics));
  }

  public void clearDiagnostics(LanguageClient client) {
    client.publishDiagnostics(new PublishDiagnosticsParams());
  }
}
