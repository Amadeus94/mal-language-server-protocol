/*
 * Copyright 2020 Foreseeti AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mal.ls.formatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import org.mal.ls.compiler.lib.CompilerException;
import org.mal.ls.compiler.lib.Lexer;
import org.mal.ls.compiler.lib.MalDiagnosticLogger;

/**
 * Code formatter for MAL. The layout algorithm is based on three simple
 * combinators; stacking, juxtaposing, and choice. The three combinators can be
 * concatenated to produce virtually any layout for the lowest cost.
 *
 * <p>
 * Yelland, P. (2016). A New Approach to Optimal Code Formatting.
 */
public class Formatter {
  public static String format(File file, int margin) throws IOException, CompilerException {
    Locale.setDefault(Locale.ROOT);
    try {
      MalDiagnosticLogger.reset();
      org.mal.ls.compiler.lib.Parser.parse(file);
      if(MalDiagnosticLogger.getInstance().messages.size() > 0){
        throw new CompilerException("Code to be formatted must be syntactically valid");
      }
    } catch (IOException e) {
      throw new CompilerException("Code to be formatted must be syntactically valid");
    }
    var p = new Parser(file);
    p.parse();
    var output = p.getOutput(margin);
    output = output.replaceAll("(?m) +$", "");
    var bytes = output.getBytes();
    var tempFile = File.createTempFile("formatted", ".tmp");
    tempFile.deleteOnExit();
    try (var fos = new FileOutputStream(tempFile)) {
      fos.write(bytes);
      if (!Lexer.syntacticallyEqual(new Lexer(file), new Lexer(tempFile))) {
        throw new CompilerException("The formatter has produced an AST that differs from the input.");
      }
    } catch (Exception e) {
      throw new CompilerException("The formatter has produced an invalid AST. Please report this as a bug.");
    }
    return output;
  }
}
