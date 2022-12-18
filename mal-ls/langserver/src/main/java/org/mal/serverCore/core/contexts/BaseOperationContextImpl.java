package org.mal.serverCore.core.contexts;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;
import org.mal.serverApi.api.ClientLogManager;
import org.mal.serverApi.api.context.BaseOperationContext;
import org.mal.serverApi.api.context.LSContext;
import org.mal.serverCore.core.utils.ClientLogManagerImpl;

public class BaseOperationContextImpl implements BaseOperationContext  {
    private final LSContext serverContext;

    public BaseOperationContextImpl(LSContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public ClientLogManager clientLogManager() {
        return ClientLogManagerImpl.getInstance(this.serverContext);
    }

    @Override
    public ClientCapabilities clientCapabilities() {
        return serverContext.getClientCapabilities().orElseThrow();
    }

    @Override
    public LanguageClient getClient() {
        return this.serverContext.getClient();
    }


    // @Override
    //public CompilerManager compilerManager() {
        //return BallerinaCompilerManager.getInstance(serverContext);
    //}

    //@Override
    //public DiagnosticsPublisher diagnosticPublisher() {
        //return DiagnosticsPublisherImpl.getInstance(serverContext);
    //}


    //@Override
    //public ConfigurationHolder clientConfigHolder() {
        //return ConfigurationHolderImpl.getInstance(serverContext);
    //}

}