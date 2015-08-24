package com.algorithmia;

import com.algorithmia.algo.*;
import com.algorithmia.client.*;
import com.algorithmia.data.*;

/**
 * Instantiate Algorithmia clients for calling algorithms and accessing data
 */
public final class Algorithmia {

    private static AlgorithmiaClient defaultClient = null;

    private Algorithmia() {} // Not instantiable

    /**
     * Returns the default Algorithmia client which will
     * look for ALGORITHMI_API_KEY environment variable or java property
     * If no key is found, then requests will be unauthenticated which only works
     * when making requests from an algorithm running within the Algorithmia cluster
     * @return an Algorithmia client
     */
    public static AlgorithmiaClient client() {
        return getDefaultClient();
    }


    /**
     * Builds an Algorithmia client that makes all requests with your API key
     * If API key is null, the default client is returned, which will
     * look for ALGORITHMI_API_KEY environment variable or java property
     * If no key is found, then requests will be unauthenticated which only works
     * when making requests from an algorithm running within the Algorithmia cluster
     * @param simpleKey API Key for simple authentication (prefixed with "sim")
     * @return an Algorithmia client
     */
    public static AlgorithmiaClient client(String simpleKey) {
        if(simpleKey == null) {
            return getDefaultClient();
        } else {
            return new AlgorithmiaClient(new SimpleAuth(simpleKey));
        }
    }

    /**
     * Initialize an Algorithm object using the default client
     * @param algoUri the algorithm's URI, e.g., algo://user/algoname
     * @return an Algorithm client for the specified algorithm
     */
    public static Algorithm algo(String algoUri) {
        return getDefaultClient().algo(algoUri);
    }

   /**
     * Initialize a DataDirectory object using the default client
     * @param path to a data directory, e.g., data://.my/foo
     * @return a DataDirectory client for the specified directory
     */
    public static DataDirectory dir(String path) {
        return getDefaultClient().dir(path);
    }

    /**
     * Initialize an DataFile object using the default client
     * @param path to a data file, e.g., data://.my/foo/bar.txt
     * @return a DataFile client for the specified file
     */
    public static DataFile file(String path) {
        return getDefaultClient().file(path);
    }

    private static AlgorithmiaClient getDefaultClient() {
        if(defaultClient == null) {
            defaultClient = new AlgorithmiaClient();
        }
        return defaultClient;
    }
}
