package algorithmia.client;

import algorithmia.APIException;
import org.apache.http.entity.ContentType;
import org.apache.http.client.methods.*;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.reflect.TypeToken;

import java.util.concurrent.Future;

public class HttpClient {
    private Auth auth;

    public HttpClient(Auth auth) {
        this.auth = auth;
    }

    public HttpResponse get(String url) throws APIException {
        try {
            return this.get(url, new BasicAsyncResponseConsumer()).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + url + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + url + " (" + e.getMessage() + ")", e);
        }
    }

    public <T> T get(String url, TypeToken<T> typeToken) throws APIException {
        try {
            return this.get(url, new HttpClientHelpers.JsonDeserializeResponseHandler<T>(typeToken)).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + url + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + url + " (" + e.getMessage() + ")", e);
        }
    }

    public <T> Future<T> get(String url, HttpAsyncResponseConsumer<T> consumer) {
        return this.execute(new HttpGet(url), consumer);
    }

    public HttpResponse post(String url, HttpEntity data) throws APIException {
        try {
            return this.post(url, data, new BasicAsyncResponseConsumer()).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + url + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + url + " (" + e.getMessage() + ")", e);
        }
    }

    public <T> Future<T> post(String url, HttpEntity data, HttpAsyncResponseConsumer<T> consumer) {
        final HttpPost request = new HttpPost(url);
        request.setEntity(data);
        return this.execute(request, consumer);
    }

    public HttpResponse put(String url, HttpEntity data) throws APIException {
        try {
            return this.put(url, data, new BasicAsyncResponseConsumer()).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + url + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + url + " (" + e.getMessage() + ")", e);
        }
    }

    public <T> Future<T> put(String url, HttpEntity data, HttpAsyncResponseConsumer<T> consumer) {
        final HttpPut request = new HttpPut(url);
        request.setEntity(data);
        return this.execute(request, consumer);
    }


    public HttpResponse delete(String url) throws APIException {
        try {
            return this.delete(url, new BasicAsyncResponseConsumer()).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + url + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + url + " (" + e.getMessage() + ")", e);
        }
    }

    public <T> Future<T> delete(String url, HttpAsyncResponseConsumer<T> consumer) {
        final HttpDelete request = new HttpDelete(url);
        return this.execute(request, consumer);
    }

    public HttpResponse head(String url) throws APIException {
        try {
            return this.head(url, new BasicAsyncResponseConsumer()).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + url + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + url + " (" + e.getMessage() + ")", e);
        }
    }

    public <T> Future<T> head(String url, HttpAsyncResponseConsumer<T> consumer) {
        final HttpHead request = new HttpHead(url);
        return this.execute(request, consumer);
    }

    private <T> Future<T> execute(HttpUriRequest request, HttpAsyncResponseConsumer<T> consumer) {
        this.auth.authenticateRequest(request);

        HttpHost target = new HttpHost(request.getURI().getHost());
        final CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
        client.start();
        return client.execute(new BasicAsyncRequestProducer(target, request), consumer, null);
    }


}