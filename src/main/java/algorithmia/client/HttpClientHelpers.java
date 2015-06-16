package algorithmia.client;

import algorithmia.algo.AlgoResponse;
import algorithmia.APIException;
import algorithmia.util.JsonHelpers;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

/**
 * Various HTTP actions, using our HttpClient class, and automatically adding authorization
 */
public class HttpClientHelpers {
    private HttpClientHelpers() {}  // non-instantiable

    /**
     * Helper class to handle an async HTTP response, and notify a future
     */
    public static class AlgoAsyncCallback implements FutureCallback<HttpResponse> {
        private final String url;
        private final CompletableFuture<AlgoResponse> promise;

        public AlgoAsyncCallback(String url, CompletableFuture<AlgoResponse> promise) {
            this.url = url;
            this.promise = promise;
        }

        public void cancelled() {
            promise.cancel(true);
        }
        public void completed(HttpResponse response) {
            // Handle response
            final int status = response.getStatusLine().getStatusCode();
            final HttpEntity entity = response.getEntity();
            if(200 <= status && status < 300) {
                try {
                    // Read response
                    final InputStream is = entity.getContent();
                    // Parse JSON response
                    final JsonParser parser = new JsonParser();
                    final JsonElement outputJson = parser.parse(new InputStreamReader(is));

                    // Parse JSON RPC style result
                    try {
                        final AlgoResponse result = JsonHelpers.jsonToAlgoResponse(outputJson);
                        promise.complete(result);
                    } catch(APIException e) {
                        promise.completeExceptionally(e);
                    }
                } catch(IOException ex) {
                    promise.completeExceptionally(ex);
                }
            } else {
                promise.completeExceptionally(APIException.fromHttpResponse(response, url));

            }
        }
        public void failed(Exception ex) {
            promise.completeExceptionally(ex);
        }
    }


    /**
     * Helper class to handle an async HTTP response, and notify a future
     */
    public static class JsonAsyncCallback implements FutureCallback<HttpResponse> {
        private final String url;
        private final CompletableFuture<JsonElement> promise;

        public JsonAsyncCallback(String url, CompletableFuture<JsonElement> promise) {
            this.url = url;
            this.promise = promise;
        }

        public void cancelled() {
            promise.cancel(true);
        }
        public void completed(HttpResponse response) {
            try {
                JsonElement json = parseResponseJson(response);
                promise.complete(json);
            } catch (Exception ex) {
                promise.completeExceptionally(ex);
            }
        }
        public void failed(Exception ex) {
            promise.completeExceptionally(ex);
        }
    }

    /**
     * Helper class to handle an async HTTP response, and notify a future
     */
    public static class AsyncCallback implements FutureCallback<HttpResponse> {
        private final String url;
        private final CompletableFuture<HttpResponse> promise;

        public AsyncCallback(String url, CompletableFuture<HttpResponse> promise) {
            this.url = url;
            this.promise = promise;
        }

        public void cancelled() {
            promise.cancel(true);
        }
        public void completed(HttpResponse response) {
            try {
                assertStatusSuccess(response);
                promise.complete(response);
            } catch (Exception ex) {
                promise.completeExceptionally(ex);
            }
        }
        public void failed(Exception ex) {
            promise.completeExceptionally(ex);
        }
    }



    public static void assertStatusSuccess(HttpResponse response) throws APIException {
        final int status = response.getStatusLine().getStatusCode();
        if(200 > status || status > 300) {
            throw APIException.fromHttpResponse(response, null);  //TODO: stop sending null URL
        }
    }

    public static JsonElement parseResponseJson(HttpResponse response) throws APIException {
        assertStatusSuccess(response);

        try {
            final HttpEntity entity = response.getEntity();
            final InputStream is = entity.getContent();
            final JsonParser parser = new JsonParser();
            return parser.parse(new InputStreamReader(is));
        } catch(IOException ex) {
            throw new APIException("IOException: " + ex.getMessage());
        }
    }

}
