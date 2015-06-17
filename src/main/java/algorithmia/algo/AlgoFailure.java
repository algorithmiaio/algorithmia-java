package algorithmia.algo;

import algorithmia.AlgorithmException;
import java.lang.reflect.Type;

/**
 * A result representing failure
 */
public final class AlgoFailure extends AlgoResponse {

    public AlgorithmException error;

    public AlgoFailure(AlgorithmException error) {
        this.error = error;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    @Override
    public Metadata metadata() throws AlgorithmException {
        throw error;
    }

    @Override
    protected <T> T as(Class<T> returnClass) throws AlgorithmException {
        throw error;
    }

    @Override
    protected <T> T as(Type returnType) throws AlgorithmException {
        throw error;
    }

}
