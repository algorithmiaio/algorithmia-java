package com.algorithmia.algo;

import com.algorithmia.TypeToken;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.apache.commons.codec.binary.Base64;

import java.lang.UnsupportedOperationException;
import java.lang.reflect.Type;

/**
 * A result representing success
 */
public final class AlgoSuccess extends AlgoResponse {

    private JsonElement result;
    private Metadata metadata;

    private transient final Gson gson = new Gson();
    private transient final Type byteType = new TypeToken<byte[]>(){}.getType();

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
    @SuppressWarnings("unchecked")
    protected <T> T as(Class<T> returnClass) {
        if(metadata.getContentType() == ContentType.Void) {
            return null;
        } else if(metadata.getContentType() == ContentType.Text) {
            return gson.fromJson(new JsonPrimitive(result.getAsString()), returnClass);
        } else if(metadata.getContentType() == ContentType.Json) {
            return gson.fromJson(result, returnClass);
        } else if(metadata.getContentType() == ContentType.Binary) {
            if(byte[].class == returnClass) {
              return (T)Base64.decodeBase64(result.getAsString());
            } else {
              throw new UnsupportedOperationException("Only support returning as byte[] for Binary data");
            }

        } else {
            throw new UnsupportedOperationException("Unknown ContentType in response: " + metadata.getContentType().toString());
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T as(Type returnType) {
        if(metadata.getContentType() == ContentType.Void) {
            return null;
        } else if(metadata.getContentType() == ContentType.Text) {
            return gson.fromJson(new JsonPrimitive(result.getAsString()), returnType);
        } else if(metadata.getContentType() == ContentType.Json) {
            return gson.fromJson(result, returnType);
        } else if(metadata.getContentType() == ContentType.Binary) {
            if(byteType.equals(returnType)) {
              return (T)Base64.decodeBase64(result.getAsString());
            } else {
              throw new UnsupportedOperationException("Only support returning as byte[] for Binary data");
            }
        } else {
            throw new UnsupportedOperationException("Unknown ContentType in response: " + metadata.getContentType().toString());
        }
    }

    @Override
    public String asJsonString() {
        return result.toString();
    }

    @Override
    public String asString() {
        return as(String.class);
    }

}
