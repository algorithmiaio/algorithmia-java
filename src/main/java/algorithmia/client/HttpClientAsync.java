package algorithmia.client;

import org.apache.http.entity.ContentType;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;

public class HttpClientAsync {
    private Auth auth;

    public HttpClientAsync(Auth auth) {
        this.auth = auth;
    }

    public Future<HttpResponse> post(String url, HttpEntity data, FutureCallback<HttpResponse> callback) {
        // Start client
        final CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
        client.start();

        // Construct request
        final HttpPost request = new HttpPost(url);
        this.auth.authenticateRequest(request);
        request.setEntity(data);

        return client.execute(request, callback);
    }


}