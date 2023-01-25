package org.mal.ls;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentFormattingParams;
import org.eclipse.lsp4j.DocumentOnTypeFormattingParams;
import org.eclipse.lsp4j.DocumentRangeFormattingParams;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.DocumentSymbolParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ReferenceParams;
import org.eclipse.lsp4j.RenameParams;
import org.eclipse.lsp4j.SignatureHelp;
import org.eclipse.lsp4j.SignatureHelpParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.mal.ls.compiler.lib.AST;
import org.mal.ls.compiler.lib.CompilerException;
import org.mal.ls.compiler.lib.MalDiagnosticLogger;
import org.mal.ls.compiler.lib.Parser;
import org.mal.ls.context.ContextKeys;
import org.mal.ls.context.DocumentManager;
import org.mal.ls.context.DocumentManagerImpl;
import org.mal.ls.context.LanguageServerContext;
import org.mal.ls.context.LanguageServerContextImpl;
import org.mal.ls.features.hover.HoverModel;
import org.mal.ls.features.hover.HoverProvider;
import org.mal.ls.features.reference.ReferenceProvider;
import org.mal.ls.features.symbol.MALSymbolProvider;
import org.mal.ls.features.symbol.ReferenceHandler;
import org.mal.ls.features.symbol.Symbol;
import org.mal.ls.features.completion.CompletionItemsProvider;
import org.mal.ls.features.definition.DefinitionProvider;
import org.mal.ls.features.diagnostics.DiagnosticProvider;
import org.mal.ls.features.format.FormatProvider;

/**
 * TextDocumentService is responsible for handling the communication between the
 * client and the server
 * related to text documents, such as synchronization, code completion, and
 * error checking.
 * 
 * Requests such as hover, completion, definition, etc.
 * 
 * The class contains a MalLanguageServer instance
 * which is used to send notifications to the client.
 * 
 * The class also contains a LanguageServerContext instance
 * which is used to store the context of the language server. In other words,
 * when a client sends a request such as textdocument/completion, the server
 * will store the
 * request parameters in the context. The server will then use the context
 * typically in a provider class such as ReferenceProvider to to dermine which
 * code to reference.
 * 
 */
public class MalTextDocumentService implements TextDocumentService {

    private final MalLanguageServer server;
    private final LanguageServerContext context;
    private final DocumentManager documentManager;

    private final CompletionItemsProvider ciHandler;
    private final DefinitionProvider defHandler;
    private final DiagnosticProvider diagnosticHandler;
    private final FormatProvider formatHandler;
    private final ReferenceProvider referenceProvider;

    public MalTextDocumentService(MalLanguageServer server) {
        this.server = server;
        this.context = new LanguageServerContextImpl();
        this.documentManager = new DocumentManagerImpl();
        this.ciHandler = new CompletionItemsProvider();
        this.defHandler = new DefinitionProvider();
        this.diagnosticHandler = new DiagnosticProvider();
        this.formatHandler = new FormatProvider();
        referenceProvider = new ReferenceProvider();
    }

    /**
     * Creates and returns a list of completion items
     */
    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
            CompletionParams completionParams) {
        List<CompletionItem> completionItems = new ArrayList<>();
        ciHandler.addCompletionItemASTNames(context.get(ContextKeys.AST_KEY), completionItems);
        completionItems.addAll(ciHandler.getCompletionItems());
        return CompletableFuture.supplyAsync(() -> {
            return Either.forLeft(completionItems);
        });
    }

    @Override
    public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem completionItem) {
        return null;
    }

    /**
     * 'textDocument/hover'-request
     * The client sends the HoverParams data model as input parameters
     *
     * The server can extract the document
     * URI and Position enclosed with the TextDocumentPositionParams
     *
     * The server responds with a hover-response data model
     * 
     * 1. Use the document URI to isolate and obtain the SEMANTIC MODEL to extrac
     * the the SYMBOL-information at the given CURSOR POSITION
     * 
     * Notice: The contents field can either be a
     * - 'MarkedString','MarkedString[]' or 'MarkupContent' // MarkedString
     * structure has been deprecated
     */
    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                Hover hover = new Hover();
                context.put(ContextKeys.URI_KEY, params.getTextDocument().getUri());

                AST ast = context.get(ContextKeys.AST_KEY);

                // 1: Fill
                List<HoverModel> hoverModels = HoverProvider.fillItemsList(ast);

                // 2. Compare
                Position hoverPosition = params.getPosition();

                MALSymbolProvider symbolProvider = new MALSymbolProvider();
                List<Symbol> symbols = symbolProvider.getSymbols(ast);
                Symbol symbolAtCursor = getSymbol(hoverPosition, symbols);

                // 3. Get the hover model
                HoverModel model = new HoverModel(new Range(), "", "", "");
                for (HoverModel hoverModel : hoverModels) {
                    if (hoverModel.id.equals(symbolAtCursor.getName())) {
                        model = hoverModel;
                    }
                }
                // 3.1. If no hover model is found, return empty
                if (model.id.equals("")) {
                    return hover;
                }

                // 4. Prepare result
                String result = model.typeDescription; 

                MarkupContent content = new MarkupContent();
                content.setKind(MarkupKind.MARKDOWN);
                content.setValue(result);
                hover.setContents(content);

                return hover;

            } catch (Throwable e) {
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<SignatureHelp> signatureHelp(SignatureHelpParams params) {
        return null;
    }

    /**
     * Identifies token for location and returns location were token is
     * initlized 'textDocument/-definition"-request
     *
     * Step 1: Identify which token the user has executed the go to definition
     * on
     *
     *
     * The position of where the user has the cursor when the function is called
     * is included as a parameter to the "textDocument/-definition"-request -
     * along with a locally saved syntax tree of the current file along with its
     * included files, the language server examines whether the cursor position
     * either falls for an association-referenced asset, a Leads to, or a
     * Requires. - If this is not the case, the return value is set to an empty
     * list. - However, if the cursor position falls into any of the three
     * aforementioned locations the token name is saved and checked against
     * various category, asset, attack step, and let variables names from the
     * saved syntax tree th server responds with a list of positiosn that match
     * the previously token name.
     *
     */
    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
            DefinitionParams params) {
        context.put(ContextKeys.URI_KEY, params.getTextDocument().getUri());
        List<Location> locationList = new ArrayList<>();
        return CompletableFuture.supplyAsync(() -> {
            String variable = this.defHandler.getVariable(params.getPosition(), context.get(ContextKeys.AST_KEY));
            if (!variable.equals("")) {
                locationList.addAll(this.defHandler.getDefinitionLocations(context.get(ContextKeys.URI_KEY)));
            }
            return Either.forLeft(locationList);
        });

    }

    private String getDefinitionUri(String fileName, String uri) {
        StringBuilder sb = new StringBuilder();
        String[] path = uri.split("/");
        for (int i = 0; i < path.length - 1; i++) {
            sb.append(path[i]);
            sb.append("/");
        }
        sb.append(fileName);
        return sb.toString();
    }

    /**
     * textDocument/reference - request
     * 
     * The request is sent from the server to the lcient to get all the references
     * of a symbol
     * 
     * For a given source file
     * compilers generate symbols including semantic information
     * ^
     * 
     */
    @Override
    public CompletableFuture<List<? extends Location>> references(ReferenceParams referenceParams) {
        MALSymbolProvider symbolProvider = new MALSymbolProvider();
        context.put(ContextKeys.URI_KEY, referenceParams.getTextDocument().getUri());
        Position cursorPosition = referenceParams.getPosition();
        List<Location> locationList = new ArrayList<>();

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get the symbols from the AST
                List<Symbol> symbols = symbolProvider.getSymbols(context.get(ContextKeys.AST_KEY));
                Symbol symbolAtCursor = getSymbol(cursorPosition, symbols);

                if (symbolAtCursor == null)
                    return locationList;

                server.getClient()
                        .showMessage(
                                new MessageParams(MessageType.Info, "symbolAtCursor: " + symbolAtCursor.getName()));

                for (Symbol s : symbols) {
                    if (s.getName().equals(symbolAtCursor.getName())) {
                        locationList.add(
                                new Location(
                                        getDefinitionUri(
                                                s.getLocation().getUri(), context.get(ContextKeys.URI_KEY)),
                                        s.getLocation().getRange()));
                    }
                }

                return locationList;
            } catch (Throwable e) {
                return null;
            }
        });
    }

    private Symbol getSymbol(Position cursorPosition, List<Symbol> symbols) {
        for (Symbol s : symbols) {
            // Check whether cursor position is within the range of the symbol
            Position startPos = s.getLocation().getRange().getStart();
            Position endPos = s.getLocation().getRange().getEnd();

            int diff = endPos.getCharacter() - startPos.getCharacter();

            if (startPos.getLine() == cursorPosition.getLine()) {
                for (int j = 0; j < diff; j++) {
                    if (cursorPosition.getCharacter() == startPos.getCharacter() + j) {
                        return s;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(
            DocumentSymbolParams params) {
        return null;
    }

    @Override
    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params) {
        return null;
    }

    @Override
    public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams codeLensParams) {
        return null;
    }

    @Override
    public CompletableFuture<CodeLens> resolveCodeLens(CodeLens codeLens) {
        return null;
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams documentFormattingParams) {
        String uri = documentFormattingParams.getTextDocument().getUri();
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<TextEdit> formatted = formatHandler.getFormatted(uri, documentManager.getContent(uri));
                documentManager.update(uri, formatted.get(0).getNewText());
                return formatted;
            } catch (URISyntaxException | IOException | CompilerException e) {
                notifyClient(e.getMessage(), MessageType.Error);
                return List.of(new TextEdit());
            }
        });
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> rangeFormatting(
            DocumentRangeFormattingParams documentRangeFormattingParams) {
        return null;
    }

    @Override
    public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(
            DocumentOnTypeFormattingParams documentOnTypeFormattingParams) {
        return null;
    }

    @Override
    public CompletableFuture<WorkspaceEdit> rename(RenameParams renameParams) {
        var newName = renameParams.getNewName();
        // renameParams.

        return null;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        documentManager.open(params.getTextDocument().getUri(), params.getTextDocument().getText());
        buildContext(params.getTextDocument().getUri());
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        documentManager.update(params.getTextDocument().getUri(), params.getContentChanges().get(0).getText());
        buildContext(params.getTextDocument().getUri());
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        documentManager.close(params.getTextDocument().getUri());
        diagnosticHandler.clearDiagnostics(server.getClient());
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        buildContext(params.getTextDocument().getUri());
    }

    private void buildContext(String uri) {
        context.put(ContextKeys.URI_KEY, uri);
        MalDiagnosticLogger.reset();
        try {
            AST ast = Parser.parse(context.get(ContextKeys.URI_KEY));
            context.put(ContextKeys.AST_KEY, ast);
        } catch (IOException | URISyntaxException e) {
            // TODO log error
        } finally {
            diagnosticHandler.sendDiagnostics(server.getClient(), context);
        }
    }

    private void notifyClient(String message, MessageType type) {
        server.getClient().showMessage(new MessageParams(type, message));
    }
}
