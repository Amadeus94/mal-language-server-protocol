package org.mal.serverApi.api.context;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;
import org.mal.serverApi.api.ClientLogManager;

public interface BaseOperationContext {

   // CompilerManager compilerManager();

    //DiagnosticsPublisher diagnosticPublisher();

    //ConfigurationHolder clientConfigHolder();

    ClientLogManager clientLogManager();
    
    ClientCapabilities clientCapabilities();
    
    LanguageClient getClient();
}
