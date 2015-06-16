package algorithmia.algo;

import algorithmia.APIException;
import algorithmia.client.HttpClient;
import algorithmia.client.HttpClientHelpers.AlgoAsyncCallback;
import algorithmia.util.JsonHelpers;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.Future;
import java.util.concurrent.CompletableFuture;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Generic algorithm interface. All algorithms in Algorithmia derive from this class.
 */
public class Algorithm {
    private AlgorithmRef algoRef;
    private HttpClient client;

    public Algorithm(HttpClient client, String algoUri) {
        this.client = client;
        this.algoRef = new AlgorithmRef(algoUri);
    }

    public Algorithm(HttpClient client, String username, String algoname) {
        this(client, username, algoname, Version.Latest());
    }

    public Algorithm(HttpClient client, String username, String algoname, Version version) {
        this.client = client;
        this.algoRef = new AlgorithmRef(username, algoname, version);
    }

    /**
     * Calls the Algorithmia API on a given input.
     * Attempts to automatically format the input as JSON.
     *
     * @param algoRef identifier of the algorithm to call (eg- "/kenny/Dijkstra")
     * @param input algorithm input, will automatically be converted into JSON
     * @return algorithm result (AlgoSuccess or AlgoFailure)
     * @throws APIException if there is a problem communication with the Algorithmia API.
     */
    public AlgoResponse pipe(Object input) throws APIException {
        final Gson gson = new Gson();
        final JsonElement inputJson = gson.toJsonTree(input);
        final AlgoResponse result = this.pipeJson(inputJson);
        return result;
    }

   /**
     * Calls the Algorithmia API on a given input asynchronously.
     *
     * @param algoRef identifier of the algorithm to call (eg- "/kenny/Dijkstra")
     * @param input algorithm input, will automatically be converted into JSON
     * @return future algorithm result
     */
    // private Future<AlgoResponse> pipeAsync(Object input) {
    //     final Gson gson = new Gson();
    //     final JsonElement inputJson = gson.toJsonTree(input);
    //     final Future<AlgoResponse> result = this.pipeJsonAsync(inputJson);
    //     return result;
    // }


    /**
     * Run an algorithm on JSON input directly
     * @param algo algorithm reference to call
     * @param inputJson json input value
     * @return success or failure
     * @throws APIException if there is a problem communication with the Algorithmia API.
     */
    private AlgoResponse pipeJson(JsonElement inputJson) throws APIException {
        try {
            return pipeJsonAsync(inputJson).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + this.algoRef.url() + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + this.algoRef.url() + " (" + e.getMessage() + ")", e);
        }
    }

    /**
     * Run an algorithm on JSON input directly
     * @param algo algorithm reference to call
     * @param inputJson json input value
     * @return success or failure
     */
    private Future<AlgoResponse> pipeJsonAsync(JsonElement inputJson) {
        final CompletableFuture<AlgoResponse> promise = new CompletableFuture<AlgoResponse>();

        this.client.post(
            this.algoRef.url(),
            new StringEntity(inputJson.toString(), ContentType.APPLICATION_JSON),
            new AlgoAsyncCallback(this.algoRef.url(), promise)
        );

        return promise;
    }


}
