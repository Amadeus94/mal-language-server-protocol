package org.mal.ls;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.WorkspaceSymbolParams;
import org.eclipse.lsp4j.services.WorkspaceService;

/**
 * MalWorkspaceService is used to handle workspace requests 
 * 
 * Examples of requests from the client to the server:
 * - textworkspace/didChangeConfiguration
 * - textworkspace/didChangeWatchedFiles
 * 
 * etc.
 */
public class MalWorkspaceService implements WorkspaceService {
  @Override
  public CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams workspaceSymbolParams) {
    return null;
  }

  @Override
  public void didChangeConfiguration(DidChangeConfigurationParams didChangeConfigurationParams) {

  }

  @Override
  public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {

  }
}
