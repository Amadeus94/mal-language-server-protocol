package org.mal.serverCore.core;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;
import org.mal.serverApi.api.context.LSContext;


public class MalLSContext implements LSContext {
    private Map<LSContext.Key<?>, Object> props = new HashMap<>();
    private Map<Class<?>, Object> objects = new HashMap<>();
    private LanguageClient languageClient;
    private ClientCapabilities clientCapabilities;

    public <V> void put(LSContext.Key<V> key, V value) {
        props.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <V> V get(LSContext.Key<V> key) {
        return (V) props.get(key);
    }

    public <V> void put(Class<V> clazz, V value) {
        objects.put(clazz, value);
    }

    @SuppressWarnings("unchecked")
    public <V> V get(Class<V> clazz) {
        return (V) objects.get(clazz);
    }

    @Override
    public void setClient(LanguageClient client) {
        this.languageClient = client;
    }

    @Override
    public LanguageClient getClient() {
        return this.languageClient;
    }

    @Override
    public void setClientCapabilities(ClientCapabilities capabilities) {
        this.clientCapabilities = capabilities;
    }

    @Override
    public Optional<ClientCapabilities> getClientCapabilities() {
        return Optional.ofNullable(this.clientCapabilities);
    }
}
