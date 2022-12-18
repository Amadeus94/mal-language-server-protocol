package org.mal.serverCore.core.contexts;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;
import org.mal.serverApi.api.ClientLogManager;
import org.mal.serverApi.api.context.LSContext;
import org.mal.serverApi.api.context.MalTextDocumentContext;



public class MalTextDocumentContextImpl extends BaseOperationContextImpl implements MalTextDocumentContext{
    private final LSContext serverContext;
    private final String uri;


    public MalTextDocumentContextImpl(LSContext serverContext, String uri) {
        super(serverContext);
        this.serverContext = serverContext;
        this.uri = uri;
    }
    

    @Override
    public ClientLogManager clientLogManager() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public ClientCapabilities clientCapabilities() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public LanguageClient getClient() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Path getPath() {
        URI pathUri = URI.create(uri);
        return Paths.get(pathUri);
    }
    
}
