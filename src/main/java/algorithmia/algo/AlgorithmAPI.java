package algorithmia.algo;

import algorithmia.AlgorithmiaConf;
import algorithmia.APIException;
import algorithmia.util.JsonHelpers;
import algorithmia.util.HttpClientAsyncHelpers.AsyncHttpCallbackJson;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.CompletableFuture;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

/**
 * Tools to make queries to Algorithmia, either locally or API
 */
public final class AlgorithmAPI {
    private AlgorithmAPI() {}  // non-instantiable

    /**
     * Run an algorithm on JSON input directly
     * @param algo algorithm reference to call
     * @param inputJson json input value
     * @return success or failure
     * @throws APIException if there is a problem communication with the Algorithmia API.
     */
    public static AlgoResponse callJson(AlgorithmRef algo, JsonElement inputJson) throws APIException {
        try {
            return callJsonAsync(algo, inputJson).get();
        } catch(java.util.concurrent.ExecutionException e) {
            Throwable cause = e.getCause();
            throw new APIException(cause.getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + algo.url() + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + algo.url() + " (" + e.getMessage() + ")", e);
        }
    }

    /**
     * Run an algorithm on JSON input directly
     * @param algo algorithm reference to call
     * @param inputJson json input value
     * @return success or failure
     */
    private static CompletableFuture<AlgoResponse> callJsonAsync(AlgorithmRef algo, JsonElement inputJson) {
        // Start client
        final CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
        client.start();

        // Construct request
        final String url = algo.url();
        final HttpPost post = new HttpPost(url);
        if(AlgorithmiaConf.apiKey() != null) {
          post.addHeader("Authorization", "Simple " + AlgorithmiaConf.apiKey());
        }
        post.setEntity(new StringEntity(inputJson.toString(), ContentType.APPLICATION_JSON));

        // Get future result
        final CompletableFuture<AlgoResponse> promise = new CompletableFuture<>();
        client.execute(post, new AsyncHttpCallbackJson(url, promise));
        return promise;
    }

}
