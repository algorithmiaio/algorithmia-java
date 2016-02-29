package com.algorithmia.algo;

import com.algorithmia.AlgorithmException;

import java.lang.reflect.Type;

/**
 * A result representing success of an asynchronous algo call
 */
public class AlgoAsyncResponse {
    private String async;
    private String request_id;

    public AlgoAsyncResponse(String async, String requestId) {
        this.async = async;
        this.request_id = requestId;
    }

    public String getAsyncProtocol() {
        return async;
    }

    public String getRequestId() {
        return request_id;
    }

}
