package algorithmia.algo;

import algorithmia.APIException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.concurrent.CompletableFuture;


/**
 * Generic algorithm interface. All algorithms in Algorithmia derive from this class.
 */
public class Algorithm {
    private AlgorithmRef algoRef;

    public Algorithm(String algoUri) {
        this.algoRef = new AlgorithmRef(algoUri);
    }

    public Algorithm(String username, String algoname) {
        this(username, algoname, Version.Latest());
    }

    public Algorithm(String username, String algoname, Version version) {
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
        final AlgoResponse result = AlgorithmAPI.callJson(this.algoRef, inputJson);
        return result;
    }

   /**
     * Calls the Algorithmia API on a given input asynchronously.
     *
     * @param algoRef identifier of the algorithm to call (eg- "/kenny/Dijkstra")
     * @param input algorithm input, will automatically be converted into JSON
     * @return future algorithm result
     */
    // private CompletableFuture<AlgoResponse> pipeAsync(Object input) {
    //     final Gson gson = new Gson();
    //     final JsonElement inputJson = gson.toJsonTree(input);
    //     final CompletableFuture<AlgoResponse> result = AlgorithmAPI.callJsonAsync(this.algoRef, inputJson);
    //     return result;
    // }


}
