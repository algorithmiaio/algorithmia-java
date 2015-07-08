package com.algorithmia;


/**
 * Stores configuration for the Algorithmia client library
 */
public final class AlgorithmiaConf {
    private AlgorithmiaConf() {}

    private static String apiAddress;

    /**
     * Returns the base address of the API server (https://api.algorithmia.com)
     * @return the base address of the API server
     */
    public static String apiAddress() {
        // Cache the API Address
        if(apiAddress == null) {
            apiAddress = getConfigValue("ALGORITHMIA_API");
        }
        if(apiAddress == null) {
            apiAddress = "https://api.algorithmia.com";
        }

        return apiAddress;
    }

    // Only used by the default AlgorithmiaClient
    protected static String apiKey() {
        return getConfigValue("ALGORITHMIA_API_KEY");
    }

    private static String getConfigValue(String configKey) {
        final String envVal = System.getenv(configKey);
        final String propVal = System.getProperty(configKey);
        String retVal = null;

        if(propVal != null && propVal.trim().length() > 0) {
            retVal = propVal.trim();
        } else if(envVal != null && envVal.trim().length() > 0) {
            retVal = envVal.trim();
        }
        return retVal;
    }
}
