package com.algorithmia.algo;

import com.algorithmia.AlgorithmiaConf;

/**
 * A reference to an algorithm and possibly a specific version.
 */
public class AlgorithmRef {
    public final String algoUri;

    /**
     * Build an AlgorithmRef from an algorithm url like "/user/algo/version"
     * @param algoUri a string reference to an algorithm of the form "/user/algo" or "/user/algo/version"
     */
    public AlgorithmRef(String algoUri) {
        this.algoUri = algoUri.replaceAll("^algo://|^/", "");
    }

    /**
     * Resolves this file reference into an HTTP url
     * @return the HTTP url for this file
     */
    public String getUrl() {
        return AlgorithmiaConf.apiAddress() + "/v1/algo/" + algoUri;
    }

    @Override
    public String toString() {
        return "algo://"+algoUri;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

}
