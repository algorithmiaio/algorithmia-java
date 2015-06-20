package com.algorithmia;


/**
 * Stores configuration for the Algorithmia client library
 */
public class AlgorithmiaConf {
    private AlgorithmiaConf() {}

    private static String apiAddress;

    /**
     * Returns the base address of the API server (https://api.algorithmia.com)
     * @return the base address of the API server
     */
    public static String apiAddress() {
        // Cache the API Address
        if(apiAddress == null) {
            // System environment variable
            final String envApiAddress = System.getenv("ALGORITHMIA_API");
            // Java property variable
            final String propertyApiAddress = System.getProperty("ALGORITHMIA_API");
            if(propertyApiAddress != null && propertyApiAddress.trim().length() > 0) {
                apiAddress = propertyApiAddress;
            } else if(envApiAddress != null && envApiAddress.trim().length() > 0) {
                apiAddress = envApiAddress;
            } else {
                // Default to official Algorithmia API
                apiAddress = "https://api.algorithmia.com";
            }
        }
        return apiAddress;
    }

}
