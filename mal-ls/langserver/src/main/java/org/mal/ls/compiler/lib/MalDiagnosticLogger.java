package org.mal.ls.compiler.lib;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.lsp4j.DiagnosticSeverity;

public class MalDiagnosticLogger {
  public Set<MalDiagnostic> messages;
  private static MalDiagnosticLogger instance = null;

  public MalDiagnosticLogger() {
    this.messages = new TreeSet<>();
  }

  public static MalDiagnosticLogger getInstance() {
    if (instance == null)
      instance = new MalDiagnosticLogger();
    return instance;
  }

  public static void reset() {
    instance = null;
  }

  public void error(MalLocation location, String message) {
    addDiagnostic(location, message, DiagnosticSeverity.Error);
  }

  public void warn(MalLocation location, String message) {
    addDiagnostic(location, message, DiagnosticSeverity.Warning);
  }

  public void info(MalLocation location, String message) {
    addDiagnostic(location, message, DiagnosticSeverity.Information);
  }

  public void hint(MalLocation location, String message) {
    addDiagnostic(location, message, DiagnosticSeverity.Hint);
  }

  private void addDiagnostic(MalLocation location, String message, DiagnosticSeverity severity) {
    this.messages.add(new MalDiagnostic(location, message, severity));
  }
}
