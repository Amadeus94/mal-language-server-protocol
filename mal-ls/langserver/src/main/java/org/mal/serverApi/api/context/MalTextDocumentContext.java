package org.mal.serverApi.api.context;

import java.nio.file.Path;

public interface MalTextDocumentContext extends BaseOperationContext {
    /**
     * Get the current document path.
     *
     * @return {@link Path}
     */
    Path getPath();
    
    //Optional<Document> currentDocument();  Document is a Ballerina Class
    
    //Optional<SyntaxTree> currentSyntaxTree(); Syntax is a Ballerina Class
}
