package org.mal.ls;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ReferenceOptions;
import org.eclipse.lsp4j.RenameOptions;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.mal.ls.utils.ServerInitUtils;


/**
 * The main class of the language server.
 * 
 * Responsible for initializing the server and registering the services.
 *  such as textDocumentService and workspaceService.
 * 
 * The language server is initialized by the client 
 * by starting the server in a process, and sending an 'initialize request' to the server
 * which contains the client capabilities.
 * 
 * The server responds with an 'initialize result' which contains the server capabilities.
 * 
 * The server is then ready to receive requests from the client.
 * 
 * The MallanguageServer class uses the following classes: 
 * - MalTextDocumentService
 * - MalWorkspaceService
 * - MalLanguageClient
 * 
 * MaltextDocumentService is used to handle textDocument requests such as hover, completion, etc.
 * 
 * MalWorkspaceService is used to handle workspace requests such as didChangeConfiguration, didChangeWatchedFiles, etc.
 * 
 * MalLanguageClient is the client that is connected to the server
 * and is used to send notifications to the client and receive requests from the client.
 * 
 * Examples of requests from the client to the server:
 * - textDocument/hover
 * - textDocument/completion
 * - textworkspace/didChangeConfiguration
 * - textworkspace/didChangeWatchedFiles
 * 
 * etc.
 * 
 */
public class MalLanguageServer implements LanguageServer, LanguageClientAware {
  private TextDocumentService textDocumentService;  
  private WorkspaceService workspaceService;       
  private LanguageClient client;                    
  private int errorCode = 1;

  public MalLanguageServer() {
    this.textDocumentService = new MalTextDocumentService(this);
    this.workspaceService = new MalWorkspaceService();
  }

  /**
   * Initialize the server. 
   * 
   * The initialize request is sent as the first request from the client to the server.
   * 
   * The server can do the following in the initialize request:
   * - register capabilities
   * - register features
   * - register utility
   * 
   * @param initializeParams contains the client capabilities     
   *
   * @return the initialize result which contains the server capabilities
   */
  @Override
  public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
    //ServerInitUtils utils = new  ServerInitUtils();; 
    final InitializeResult initializeResult = new InitializeResult(new ServerCapabilities());

    initializeResult.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);
    CompletionOptions completionOptions = new CompletionOptions();
    initializeResult.getCapabilities().setCompletionProvider(completionOptions);
    initializeResult.getCapabilities().setDefinitionProvider(Boolean.TRUE);
    initializeResult.getCapabilities().setDocumentFormattingProvider(Boolean.TRUE);

    // Rename Options
    //RenameOptions renameOptions = new RenameOptions();
    //renameOptions.setPrepareProvider(true); // not sure if should include
    initializeResult.getCapabilities().setRenameProvider(true);

    // Register Hover
    initializeResult.getCapabilities().setHoverProvider(Either.forRight(ServerInitUtils.getHoverOptions()));
    // What does Either.forRight do? 

    // References
    ReferenceOptions referenceOptions = new ReferenceOptions();
    referenceOptions.setWorkDoneProgress(true);
    initializeResult.getCapabilities().setReferencesProvider(referenceOptions);

    return CompletableFuture.supplyAsync(() -> initializeResult);
  }

  @Override
  public CompletableFuture<Object> shutdown() {
    errorCode = 0;
    return null;
  }

  @Override
  public void exit() {
    System.exit(errorCode);
  }

  @Override
  public TextDocumentService getTextDocumentService() {
    return this.textDocumentService;
  }

  @Override
  public WorkspaceService getWorkspaceService() {
    return this.workspaceService;
  }

  @Override
  public void connect(LanguageClient languageClient) {
    this.client = languageClient;
  }

  public LanguageClient getClient() {
    return this.client;
  }
}
