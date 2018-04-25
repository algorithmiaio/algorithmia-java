package com.algorithmia;

import com.algorithmia.algo.*;
import com.algorithmia.client.*;
import com.algorithmia.data.*;

import java.io.IOException;

/**
 * Instantiate Algorithmia clients for calling algorithms and accessing data
 */
public final class AlgorithmiaClient {
    private HttpClient client;
    /**
     * Instantiate Algorithmia client with the given auth and max number of connections
     * @param auth Algorithmia Auth object, a null auth object is valid, though only
     * correct for within the Algorithmia platform
     * @param maxConnections
     */
    protected AlgorithmiaClient(Auth auth, String apiAddress, int maxConnections) {
        this.client = new HttpClient(auth, apiAddress, maxConnections);
    }

    /**
     * Initialize an Algorithm object from this client
     * @param algoUri the algorithm's URI, e.g., algo://user/algoname
     * @return an Algorithm client for the specified algorithm
     */
    public Algorithm algo(String algoUri) {
        return new Algorithm(client, new AlgorithmRef(algoUri));
    }

    /**
     * Initialize a DataDirectory object from this client
     * @param path to a data directory, e.g., data://.my/foo
     * @return a DataDirectory client for the specified directory
     */
    public DataDirectory dir(String path) {
        return new DataDirectory(client, path);
    }

    /**
     * Initialize an DataFile object from this client
     * @param path to a data file, e.g., data://.my/foo/bar.txt
     * @return a DataFile client for the specified file
     */
    public DataFile file(String path) {
        return new DataFile(client, path);
    }

    public void close() throws IOException {
        this.client.close();
    }
}
