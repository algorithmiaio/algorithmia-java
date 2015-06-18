package com.algorithmia.client;

import org.apache.http.HttpRequest;

/**
 * A result representing success
 */
public abstract class Auth {
    protected abstract void authenticateRequest(HttpRequest request);
}
