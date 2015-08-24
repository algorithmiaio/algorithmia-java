package com.algorithmia.algo;

import com.algorithmia.APIException;
import com.algorithmia.client.HttpClient;
import com.algorithmia.client.HttpClientHelpers.AlgoResponseHandler;

import java.util.concurrent.Future;

import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * Represents an Algorithmia algorithm that can be called.
 */
public final class Algorithm {
    private AlgorithmRef algoRef;
    private HttpClient client;

    final static Gson gson = new Gson();

    public Algorithm(HttpClient client, AlgorithmRef algoRef) {
        this.client = client;
        this.algoRef = algoRef;
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
            new AlgoResponseHandler()
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
            new AlgoResponseHandler()
        );
        return new FutureAlgoResponse(promise);
    }

}
