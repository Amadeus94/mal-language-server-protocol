package org.mal.serverApi.api.context;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;

import java.util.Optional;

/**
 * Mal language server context information.
 *
 * @since 1.0.0
 */
public interface LSContext {

    <V> void put(LSContext.Key<V> key, V value);

    <V> V get(LSContext.Key<V> key);

    <V> void put(Class<V> clazz, V value);

    <V> V get(Class<V> clazz);

    void setClient(LanguageClient client);

    LanguageClient getClient();

    void setClientCapabilities(ClientCapabilities capabilities);

    Optional<ClientCapabilities> getClientCapabilities();

    /**
     * @param <K> key
     * @since 1.0.0
     */
    class Key<K> {
    }
}