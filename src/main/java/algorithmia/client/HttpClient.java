package algorithmia.client;

import algorithmia.APIException;
import org.apache.http.entity.ContentType;
import org.apache.http.client.methods.*;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;

public class HttpClient {
    private Auth auth;

    public HttpClient(Auth auth) {
        this.auth = auth;
    }

    public HttpResponse get(String url) throws APIException {
        try {
            return this.get(url, null).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + url + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + url + " (" + e.getMessage() + ")", e);
        }
    }

    public Future<HttpResponse> get(String url, FutureCallback<HttpResponse> callback) {
        final HttpGet request = new HttpGet(url);
        return this.execute(request, callback);
    }


    public HttpResponse post(String url, HttpEntity data) throws APIException {
        try {
            return this.post(url, data, null).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + url + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + url + " (" + e.getMessage() + ")", e);
        }
    }

    public Future<HttpResponse> post(String url, HttpEntity data, FutureCallback<HttpResponse> callback) {
        final HttpPost request = new HttpPost(url);
        request.setEntity(data);
        return this.execute(request, callback);
    }

    public HttpResponse put(String url, HttpEntity data) throws APIException {
        try {
            return this.put(url, data, null).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + url + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + url + " (" + e.getMessage() + ")", e);
        }
    }

    public Future<HttpResponse> put(String url, HttpEntity data, FutureCallback<HttpResponse> callback) {
        final HttpPut request = new HttpPut(url);
        request.setEntity(data);
        return this.execute(request, callback);
    }


    public HttpResponse delete(String url) throws APIException {
        try {
            return this.delete(url, null).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + url + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + url + " (" + e.getMessage() + ")", e);
        }
    }

    public Future<HttpResponse> delete(String url, FutureCallback<HttpResponse> callback) {
        final HttpDelete request = new HttpDelete(url);
        return this.execute(request, callback);
    }

    public HttpResponse head(String url) throws APIException {
        try {
            return this.head(url, null).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + url + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + url + " (" + e.getMessage() + ")", e);
        }
    }

    public Future<HttpResponse> head(String url, FutureCallback<HttpResponse> callback) {
        final HttpHead request = new HttpHead(url);
        return this.execute(request, callback);
    }



    private Future<HttpResponse> execute(HttpUriRequest request, FutureCallback<HttpResponse> callback) {
        this.auth.authenticateRequest(request);

        final CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
        client.start();
        return client.execute(request, callback);
    }
}