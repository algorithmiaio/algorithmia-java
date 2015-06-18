package com.algorithmia;


/**
 * Stores the Algorithmia SDK Configuration
 */
public class AlgorithmiaConf {
    private AlgorithmiaConf() {}

    /**
     * Returns the base address of the API server (http://api.algorithmia.com)
     * @return the base address of the API server
     */
    public static String apiAddress() {
        // System environment variable
        final String envApiAddress = System.getenv("ALGORITHMIA_API");
        // Java property variable
        final String propertyApiAddress = System.getProperty("ALGORITHMIA_API");
        if(propertyApiAddress != null && propertyApiAddress.trim().length() > 0) {
            return propertyApiAddress;
        } else if(envApiAddress != null && envApiAddress.trim().length() > 0) {
            return envApiAddress;
        } else {
            // Default to official Algorithmia API
            return "http://api.algorithmia.com";
        }
    }

}
