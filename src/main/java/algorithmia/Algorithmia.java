package algorithmia;

import algorithmia.algo.*;
import algorithmia.client.*;
import algorithmia.data.*;
import algorithmia.APIException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;

/**
 * Algorithmia main class for running and testing algorithms
 */
public final class Algorithmia {
    private HttpClient client;
    /**
     * Instantiate Algorithmia client without credentials
     * This only works for when running the client on top of the Algorithmia platform
     */
    public Algorithmia() {}

    /**
     * Instantiate Algorithmia client without Simple Key Auth
     */
    public Algorithmia(String simpleKey) {
        this.client = new HttpClient(new SimpleAuth(simpleKey));
    }

    /**
     * Initialize an Algorithm object from this client
     */
    public Algorithm algo(String algoUri) {
        return new Algorithm(client, algoUri);
    }

    public DataDirectory dir(String path) {
        return new DataDirectory(client, path);
    }

    public DataFile file(String path) {
        return new DataFile(client, path);
    }
}
