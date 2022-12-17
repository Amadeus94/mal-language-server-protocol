package org.mal.ls.features.hover;

import java.nio.file.Path;

import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.mal.ls.context.LanguageServerContext;

public class HoverProvider {
    public static Hover getHover(String startPos, String endPost) {
        Hover hover = new Hover();
        MarkupContent markupContent = new MarkupContent("asset", "test");
        hover.setContents(Either.forRight(markupContent));
        //hover.setRange(null);

        return hover;
    }

}
