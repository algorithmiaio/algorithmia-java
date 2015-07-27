package com.algorithmia.algo;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;

/**
 * A result representing success
 */
public final class AlgoSuccess extends AlgoResponse {

    private JsonElement result;
    private Metadata metadata;

    private final Gson gson = new Gson();

    public AlgoSuccess(JsonElement result, Metadata metadata) {
        this.result = result;
        this.metadata = metadata;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public boolean isFailure() {
        return false;
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    protected <T> T as(Class<T> returnClass) {
        return gson.fromJson(result, returnClass);
    }

    @Override
    protected <T> T as(Type returnType) {
        return gson.fromJson(result, returnType);
    }

}
