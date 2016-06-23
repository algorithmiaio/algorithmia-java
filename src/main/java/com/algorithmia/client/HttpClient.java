package com.algorithmia.client;

import com.algorithmia.APIException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.methods.AsyncByteConsumer;
import org.apache.http.nio.client.methods.ZeroCopyConsumer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.IOControl;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpHost;

import java.nio.ByteBuffer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import java.lang.InterruptedException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.gson.reflect.TypeToken;

import java.util.concurrent.Future;

public class HttpClient {

    final private Auth auth;

    private static String userAgent = "algorithmia-java/" + "1.0.8";

    private static List<CloseableHttpAsyncClient> clients = new LinkedList<CloseableHttpAsyncClient>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                synchronized (clients) {
                    for (CloseableHttpAsyncClient client : clients) {
                        try {
                            client.close();
                        } catch (IOException e) {}
                    }
                }
            }
        });
    }

    private final CloseableHttpAsyncClient client;
    public HttpClient(Auth auth, int maxConnections) {
        this.auth = auth;

        client = HttpAsyncClientBuilder.create()
            .setMaxConnTotal(maxConnections)
            .setMaxConnPerRoute(maxConnections).build();

        synchronized (clients) {
            clients.add(client);
        }
        client.start();
    }

    /**
     * Modifies request in place to add on any query parameters
     */
    private void addQueryParameters(HttpRequestBase request, Map<String, String> params) {
        if (params != null) {
            URIBuilder builder = new URIBuilder(request.getURI());
            for(Map.Entry<String, String> param : params.entrySet()) {
                builder.addParameter(param.getKey(), param.getValue());
            }
            try {
                request.setURI(builder.build());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Unable to construct API URI", e);
            }
        }
    }

    /*
    * GET requests
    */

    public HttpResponse get(String url) throws APIException {
        final HttpGet request = new HttpGet(url);
        return this.execute(request);
    }

    public <T> T get(String url, TypeToken<T> typeToken, Map<String, String> params) throws APIException {
        final HttpGet request = new HttpGet(url);
        addQueryParameters(request, params);
        return this.execute(request, new HttpClientHelpers.JsonDeserializeResponseHandler<T>(typeToken));

    }

    public <T> Future<T> get(String url, HttpAsyncResponseConsumer<T> consumer) {
        final HttpGet request = new HttpGet(url);
        return this.executeAsync(request, consumer);
    }

    public void getFile(String url, File destination) throws APIException {
        final HttpGet request = new HttpGet(url);
        this.executeGetFile(request, destination);
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
        return post(url, data, consumer, null);
    }

    public <T> Future<T> post(String url, HttpEntity data, HttpAsyncResponseConsumer<T> consumer, Map<String, String> parameters) {
        final HttpPost request = new HttpPost(url);
        request.setEntity(data);
        addQueryParameters(request, parameters);
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
        return executeHead(request);
    }

    public <T> Future<T> head(String url, HttpAsyncResponseConsumer<T> consumer) {
        final HttpHead request = new HttpHead(url);
        return executeAsync(request, consumer);
    }

    /**
     * PATCH requests
     */
    public HttpResponse patch(String url, StringEntity entity) throws APIException {
        final HttpPatch request = new HttpPatch(url);
        request.setEntity(entity);
        return this.execute(request);
    }

    /**
     * execute methods to execute a request
     */
    private HttpResponse execute(HttpUriRequest request) throws APIException {
        return execute(request, new BasicAsyncResponseConsumer());
    }

    private HttpResponse executeHead(HttpUriRequest request) throws APIException {
        // We don't use the BasicAsyncResponseConsumer because it barfs when the
        // content length is too long.
        return execute(request, new HttpResponseConsumer());
    }

    private void executeGetFile(HttpUriRequest request, File destination) throws APIException {
        // We don't use the BasicAsyncResponseConsumer because it barfs when the
        // content length is too long.
        try {
            ZeroCopyConsumer<File> consumer = new ZeroCopyConsumer<File>(destination) {
                @Override
                protected File process(final HttpResponse response, final File file, final ContentType contentType) throws Exception {
                    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                        throw new APIException("Download failed: " + response.getStatusLine());
                    }
                    return file;
                }
            };

            execute(request, consumer);
        } catch (FileNotFoundException e) {
            throw new APIException("Could not find destination file: " + destination.getAbsolutePath());
        }
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

    /**
     *   A consumer that drops the body of a response. It's useful when you just want the HTTP headers.
     */
    static class HttpResponseConsumer extends AsyncByteConsumer<HttpResponse> {
        private HttpResponse response;

        @Override
        protected void onResponseReceived(final HttpResponse response) {
            this.response = response;
        }

        @Override
        protected HttpResponse buildResult(final HttpContext context) throws Exception {
            return this.response;
        }

        @Override
        protected void onByteReceived(final ByteBuffer buf, final IOControl ioctrl) {
        }
    }

    @Override
    protected void finalize() {
        synchronized (clients) {
            clients.remove(client);
        }
    }
}
