package com.algorithmia.algo;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;

/**
 * A result representing success
 */
public final class AlgoSuccess extends AlgoResponse {

    public JsonElement result;
    public Metadata metadata;

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
    public Metadata metadata() {
        return metadata;
    }

    @Override
    protected <T> T as(Class<T> returnClass) {
        final Gson gson = new Gson();
        return gson.fromJson(result, returnClass);
    }

    @Override
    protected <T> T as(Type returnType) {
        final Gson gson = new Gson();
        return gson.fromJson(result, returnType);
    }

}
