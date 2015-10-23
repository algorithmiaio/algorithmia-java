package com.algorithmia.client;

import com.algorithmia.APIException;
import org.apache.http.client.methods.*;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpHost;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import java.lang.InterruptedException;
import java.io.IOException;

import com.google.gson.reflect.TypeToken;

import java.util.concurrent.Future;

public class HttpClient {

    final private Auth auth;

    private static String userAgent = "algorithmia-java/" + "1.0.4";

    private static CloseableHttpAsyncClient client;

    static {
        client = HttpAsyncClients.createDefault();
        client.start();

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                try {
                    client.close();
                } catch(IOException e) {}
            }
        });
    }

    public HttpClient(Auth auth) {
        this.auth = auth;
    }

    /*
    * GET requests
    */

    public HttpResponse get(String url) throws APIException {
        final HttpGet request = new HttpGet(url);
        return this.execute(request);
    }

    public <T> T get(String url, TypeToken<T> typeToken) throws APIException {
        final HttpGet request = new HttpGet(url);
        return this.execute(request, new HttpClientHelpers.JsonDeserializeResponseHandler<T>(typeToken));

    }

    public <T> Future<T> get(String url, HttpAsyncResponseConsumer<T> consumer) {
        final HttpGet request = new HttpGet(url);
        return this.executeAsync(request, consumer);
    }

    /**
     * POST requests
     */
    public HttpResponse post(String url, HttpEntity data) throws APIException {
        final HttpPost request = new HttpPost(url);
        request.setEntity(data);
        return this.execute(request);
    }

    public <T> Future<T> post(String url, HttpEntity data, HttpAsyncResponseConsumer<T> consumer) {
        final HttpPost request = new HttpPost(url);
        request.setEntity(data);
        return this.executeAsync(request, consumer);
    }

    /**
     * PUT requests
     */
    public HttpResponse put(String url, HttpEntity data) throws APIException {
        final HttpPut request = new HttpPut(url);
        request.setEntity(data);
        return this.execute(request);
    }

    public <T> Future<T> put(String url, HttpEntity data, HttpAsyncResponseConsumer<T> consumer) {
        final HttpPut request = new HttpPut(url);
        request.setEntity(data);
        return this.executeAsync(request, consumer);
    }

    /**
     * DELETE requests
     */
    public HttpResponse delete(String url) throws APIException {
        final HttpDelete request = new HttpDelete(url);
        return execute(request);
    }

    public <T> Future<T> delete(String url, HttpAsyncResponseConsumer<T> consumer) {
        final HttpDelete request = new HttpDelete(url);
        return executeAsync(request, consumer);
    }

    /**
     * HEAD requests
     */
    public HttpResponse head(String url) throws APIException {
        final HttpHead request = new HttpHead(url);
        return execute(request);
    }

    public <T> Future<T> head(String url, HttpAsyncResponseConsumer<T> consumer) {
        final HttpHead request = new HttpHead(url);
        return executeAsync(request, consumer);
    }


    /**
     * execute methods to execute a request
     */
    private HttpResponse execute(HttpUriRequest request) throws APIException {
        return execute(request, new BasicAsyncResponseConsumer());
    }

    private <T> T execute(HttpUriRequest request, HttpAsyncResponseConsumer<T> consumer) throws APIException {
        try {
            return executeAsync(request, consumer).get();
        } catch(ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(CancellationException e) {
            throw new APIException("API connection cancelled: " + request.getURI().toString() + " (" + e.getMessage() + ")", e);
        } catch(InterruptedException e) {
            throw new APIException("API connection interrupted: " + request.getURI().toString() + " (" + e.getMessage() + ")", e);
        }
    }

    private <T> Future<T> executeAsync(HttpUriRequest request, HttpAsyncResponseConsumer<T> consumer) {
        if(this.auth != null) {
            this.auth.authenticateRequest(request);
        }
        request.addHeader("User-Agent", HttpClient.userAgent);
        HttpHost target = new HttpHost(request.getURI().getHost(), request.getURI().getPort());
        return client.execute(new BasicAsyncRequestProducer(target, request), consumer, null);
    }

}