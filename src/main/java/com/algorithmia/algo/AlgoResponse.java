package com.algorithmia.algo;

import com.algorithmia.AlgorithmException;
import com.algorithmia.TypeToken;

import java.lang.reflect.Type;

/**
 * A generic result, can be either AlgoSuccess or AlgoFailure
 */
public abstract class AlgoResponse {

    public abstract boolean isSuccess();
    public abstract boolean isFailure();

    public abstract Metadata getMetadata() throws AlgorithmException;

    /**
     * Convert the result to a specific class
     * @param <T> the type that the response will be deserialized into
     * @param returnClass the class used by the deserializer (should correspond with T)
     * @return the result, if this is AlgoSuccess
     * @throws AlgorithmException the error, if this is AlgoFailure
     */
    protected abstract <T> T as(Class<T> returnClass) throws AlgorithmException;

    /**
     * Convert the result to a specific type
     * @param <T> the type that the response will be deserialized into
     * @param returnType the type used by the deserializer (should correspond with T)
     * @return the result, if this is AlgoSuccess
     * @throws AlgorithmException the error, if this is AlgoFailure
     */
    protected abstract <T> T as(Type returnType) throws AlgorithmException;


    /**
     * Convert the result to a specific type.
     * To avoid type erasure, be sure to instantiate as an anonymous class:
     * <pre>
     * {@code new TypeToken<MyClass>(){} }
     * </pre>
     * @param <T> the type that the response will be deserialized into
     * @param typeToken the type used by the deserializer (should correspond with T)
     * @return the result, if this is AlgoSuccess
     * @throws AlgorithmException the error, if this is AlgoFailure
     */
    public <T> T as(@SuppressWarnings("rawtypes") TypeToken typeToken) throws AlgorithmException {
        return this.as(typeToken.getType());
    }

    /**
     * Return JSON representation of the result.
     * @return the result, if this is AlgoSuccess
     * @throws AlgorithmException the error, if this is AlgoFailure
     */
    public abstract String asJsonString() throws AlgorithmException;

    /**
     * Return String representation of the result.
     * @return the result, if this is AlgoSuccess
     * @throws AlgorithmException the error, if this is AlgoFailure
     */
    public abstract String asString() throws AlgorithmException;

}
