package org.mal.ls.features.format;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.mal.ls.compiler.lib.CompilerException;
import org.mal.ls.formatter.Formatter;

public class FormatProvider {
  public List<TextEdit> getFormatted(String uri, String content)
      throws CompilerException, IOException, URISyntaxException {
    try {
      File file = new File(new URI(uri));
      String[] lines = content.split("\n");
      Range range = new Range(new Position(0, 0), new Position(lines.length, lines[lines.length - 1].length()));
      String formatted = Formatter.format(file, 2);
      TextEdit textEdit = new TextEdit(range, formatted);
      return List.of(textEdit);
    } catch (CompilerException | IOException | URISyntaxException e) {
      throw e;
    }
  }
}
