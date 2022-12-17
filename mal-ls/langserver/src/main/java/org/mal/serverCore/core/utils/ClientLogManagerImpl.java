package org.mal.serverCore.core.utils;

import org.mal.serverApi.api.ClientLogManager;
import org.mal.serverApi.api.context.LSContext;

public class ClientLogManagerImpl implements ClientLogManager{
    private static final LSContext.Key<ClientLogManager> CLIENT_LOG_MANAGER_KEY = new LSContext.Key<>();

    @Override
    public void publishInfo(String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void publishLog(String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void publishError(String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void publishWarning(String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void showErrorMessage(String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void showInfoMessage(String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void showLogMessage(String message) {
        // TODO Auto-generated method stub
        
    }
    
}
