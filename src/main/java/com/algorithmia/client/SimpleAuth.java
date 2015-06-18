package com.algorithmia.client;

import org.apache.http.HttpRequest;

/**
 * An Auth implementation for the Algorithmia Simple Auth API Key
 */
public final class SimpleAuth extends Auth {
    String apiKey;

    public SimpleAuth(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    protected void authenticateRequest(HttpRequest request) {
        request.addHeader("Authorization", "Simple " + this.apiKey);
    }
}
