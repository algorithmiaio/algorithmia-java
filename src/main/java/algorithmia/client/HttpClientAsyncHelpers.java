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
public class HttpClientAsyncHelpers {
    private HttpClientAsyncHelpers() {}  // non-instantiable

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
                String errorMessage = "";
                if(entity != null) {
                    try {
                        final InputStream is = entity.getContent();
                        errorMessage = ": " + IOUtils.toString(is, Charsets.UTF_8);
                    } catch(IOException e) {}
                }
                if(status == 401) {
                    promise.completeExceptionally(new APIException("401 not authorized" + errorMessage));
                } else if(status == 404) {
                    promise.completeExceptionally(new APIException("404 not found: " + url + errorMessage));
                } else if(status == 415) {
                    promise.completeExceptionally(new APIException("415 unsupported content type" + errorMessage));
                } else if(status == 504) {
                    promise.completeExceptionally(new APIException("504 server timeout" + errorMessage));
                } else {
                    promise.completeExceptionally(new APIException("Unexpected API response, status " + status + ", url " + url + errorMessage));
                }
            }
        }
        public void failed(Exception ex) {
            promise.completeExceptionally(ex);
        }
    }
}
