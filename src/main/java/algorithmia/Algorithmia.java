package algorithmia;

import algorithmia.algo.*;
import algorithmia.APIException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

/**
 * Algorithmia main class for running and testing algorithms
 */
public final class Algorithmia {
    /**
     * Instantiate Algorithmia client without credentials
     * This only works for when running the client on top of the Algorithmia platform
     */
    public Algorithmia() {}

    /**
     * Instantiate Algorithmia client without Simple Key Auth
     */
    public Algorithmia(String simpleKey) {
        AlgorithmiaConf.setApiKey(simpleKey);
    }

    /**
     * Initialize an Algorithm object from this client
     */
    public Algorithm algo(String algoUri) {
        return new Algorithm(algoUri);
    }
}
