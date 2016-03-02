package com.algorithmia.algo;

import com.algorithmia.APIException;
import com.algorithmia.client.HttpClient;
import com.algorithmia.client.HttpClientHelpers.AlgoResponseHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Represents an Algorithmia algorithm that can be called.
 */
public final class Algorithm {
    private final AlgorithmRef algoRef;
    private final HttpClient client;
    private final Map<String, String> options;
    private final AlgorithmOutputType outputType;
    final static Gson gson = new Gson();

    public Algorithm(HttpClient client, AlgorithmRef algoRef) {
        this(client, algoRef, new HashMap<String, String>());
    }

    public Algorithm(HttpClient client, AlgorithmRef algoRef, Map<String, String> options) {
        this.client = client;
        this.algoRef = algoRef;
        this.options = options;
        if (options != null && options.containsKey(AlgorithmOptions.OUTPUT.toString())) {
            this.outputType = AlgorithmOutputType.fromParameter(options.get(AlgorithmOptions.OUTPUT.toString()));
        } else {
            this.outputType = AlgorithmOutputType.DEFAULT;
        }
    }

    public Algorithm setOptions(Map<String, String> options) {
        if (options != null) {
            return new Algorithm(client, algoRef, new HashMap<String, String>(options));
        }
        return new Algorithm(client, algoRef, new HashMap<String, String>());
    }

    public Algorithm setOption(String key, String value) {
        Map<String, String> optionsClone = new HashMap<String, String>(options);
        optionsClone.put(key, value);
        return new Algorithm(client, algoRef, optionsClone);
    }

    public Algorithm setTimeout(Long timeout, TimeUnit unit) {
        Long time = unit.convert(timeout, TimeUnit.SECONDS);
        Map<String, String> optionsClone = new HashMap<String, String>(options);
        optionsClone.put(AlgorithmOptions.TIMEOUT.toString(), time.toString());
        return new Algorithm(client, algoRef, optionsClone);
    }

    public Algorithm setStdout(boolean showStdout) {
        Map<String, String> optionsClone = new HashMap<String, String>(options);
        optionsClone.put(AlgorithmOptions.STDOUT.toString(), Boolean.toString(showStdout));
        return new Algorithm(client, algoRef, optionsClone);
    }

    public Algorithm setOutputType(AlgorithmOutputType outputType) {
        Map<String, String> optionsClone = new HashMap<String, String>(options);
        optionsClone.put(AlgorithmOptions.OUTPUT.toString(), outputType.toString());

        return new Algorithm(client, algoRef, optionsClone);
    }

    /**
     * Calls the Algorithmia API for a given input.
     * Attempts to automatically serialize the input to JSON.
     *
     * @param input algorithm input, will automatically be converted into JSON
     * @return algorithm result (AlgoSuccess or AlgoFailure)
     * @throws APIException if there is a problem communication with the Algorithmia API.
     */
    public AlgoResponse pipe(Object input) throws APIException {
        if (input instanceof String) {
            return pipeRequest((String)input,ContentType.Text);
        } else if (input instanceof byte[]) {
            return pipeBinaryRequest((byte[])input);
        } else {
            return pipeRequest(gson.toJsonTree(input).toString(),ContentType.Json);
        }
    }

   /**
     * Calls the Algorithmia API asynchronously for a given input.
     * Attempts to automatically serialize the input to JSON.
     * The future response will complete when the algorithm has completed or errored
     *
     * @param input algorithm input, will automatically be converted into JSON
     * @return future algorithm result (AlgoSuccess or AlgoFailure)
     */
   public FutureAlgoResponse pipeAsync(Object input) {
       final Gson gson = new Gson();
       final JsonElement inputJson = gson.toJsonTree(input);
       return pipeJsonAsync(inputJson.toString());
   }


    /**
     * Calls the Algorithmia API for given input that will be treated as JSON
     *
     * @param inputJson json input value
     * @return success or failure
     * @throws APIException if there is a problem communication with the Algorithmia API.
     */
    public AlgoResponse pipeJson(String inputJson) throws APIException {
        return pipeRequest(inputJson,ContentType.Json);
    }

    /**
     * Calls the Algorithmia API asynchronously for given input that will be treated as JSON
     * The future response will complete when the algorithm has completed or errored
     *
     * @param inputJson json input value
     * @return success or failure
     */
    public FutureAlgoResponse pipeJsonAsync(String inputJson) {
        return pipeRequestAsync(inputJson,ContentType.Json);
    }

    private AlgoResponse pipeRequest(String input, ContentType content_type) throws APIException {
        try {
            return pipeRequestAsync(input,content_type).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + algoRef.getUrl() + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + algoRef.getUrl() + " (" + e.getMessage() + ")", e);
        }
    }

    private FutureAlgoResponse pipeRequestAsync(String input, ContentType content_type) {
        StringEntity requestEntity = null;
        if(content_type == ContentType.Text) {
            requestEntity = new StringEntity(input, "UTF-8");
        } else if(content_type == ContentType.Json) {
            requestEntity = new StringEntity(input, org.apache.http.entity.ContentType.APPLICATION_JSON);
        }
        Future<AlgoResponse> promise = client.post(
                algoRef.getUrl(),
                requestEntity,
                new AlgoResponseHandler(outputType),
                options
        );
        return new FutureAlgoResponse(promise);
    }

    private AlgoResponse pipeBinaryRequest(byte[] input) throws APIException {
        try {
            return pipeBinaryRequestAsync(input).get();
        } catch(java.util.concurrent.ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch(java.util.concurrent.CancellationException e) {
            throw new APIException("API connection cancelled: " + algoRef.getUrl() + " (" + e.getMessage() + ")", e);
        } catch(java.lang.InterruptedException e) {
            throw new APIException("API connection interrupted: " + algoRef.getUrl() + " (" + e.getMessage() + ")", e);
        }
    }

    private FutureAlgoResponse pipeBinaryRequestAsync(byte[] input) {
        Future<AlgoResponse> promise = client.post(
                algoRef.getUrl(),
                new ByteArrayEntity(input, org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM),
                new AlgoResponseHandler(outputType),
                options
        );
        return new FutureAlgoResponse(promise);
    }

    public static enum AlgorithmOptions {
        TIMEOUT("timeout"),
        STDOUT("stdout"),
        OUTPUT("output");
        private String parameter;

        AlgorithmOptions(String parameter) {
            this.parameter = parameter;
        }

        public String toString() {
            return this.parameter;
        }
    }

    public static enum AlgorithmOutputType {
        RAW("raw"),
        VOID("void"),
        DEFAULT("default"); // not actually an API parameter
        private String parameter;

        AlgorithmOutputType(String parameter) {
            this.parameter = parameter;
        }

        public String toString() {
            return this.parameter;
        }

        public static AlgorithmOutputType fromParameter(String parameter) {
            for (AlgorithmOutputType outputType : values()) {
                if (outputType.parameter.equals(parameter)) {
                    return outputType;
                }
            }
            return null;
        }
    }
}
