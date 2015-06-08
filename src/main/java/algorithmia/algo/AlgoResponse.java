package algorithmia.algo;

import algorithmia.AlgorithmException;
import algorithmia.TypeToken;
import com.google.gson.JsonElement;
import java.lang.reflect.Type;

/**
 * A generic result, can be either AlgoSuccess or AlgoFailure
 */
public abstract class AlgoResponse {
    public abstract boolean isSuccess();
    public abstract boolean isFailure();


    public abstract Metadata metadata() throws AlgorithmException;

    /**
     * Convert the result to a specific class
     * @return the result, if this is AlgoSuccess
     * @throws AlgorithmException the error, if this is AlgoFailure
     */
    protected abstract <T> T as(Class<T> returnClass) throws AlgorithmException;

    /**
     * Convert the result to a specific type
     * @return the result, if this is AlgoSuccess
     * @throws AlgorithmException the error, if this is AlgoFailure
     */
    protected abstract <T> T as(Type returnType) throws AlgorithmException;


    /**
     * Convert the result to a specific type without causing type erasure
     * @return the result, if this is AlgoSuccess
     * @throws AlgorithmException the error, if this is AlgoFailure
     */
    public <T> T as(TypeToken typeToken) throws AlgorithmException {
        return this.as(typeToken.getType());
    };

    /**
     * Get string representation of the result or throw an exception
     * @return the result, if this is AlgoSuccess
     * @throws AlgorithmException the error, if this is AlgoFailure
     */
    public String get() throws AlgorithmException {
        return this.as(JsonElement.class).toString();
    };


}
