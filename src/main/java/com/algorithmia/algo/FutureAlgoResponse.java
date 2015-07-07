package com.algorithmia.algo;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.lang.InterruptedException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * A request object to represent that an algorithm has been call asynchronously
 * Wrapping {@code Future<AlgoResponse> } will allow other async scenarios
 * (e.g. where the internal implementation is based on polling instead of long-lived connections)
 * without changing the exposed interface
 */
public class FutureAlgoResponse implements Future<AlgoResponse> {
    protected Future<AlgoResponse> promise;

    protected FutureAlgoResponse () {}

    public FutureAlgoResponse(Future<AlgoResponse> promise) {
        this.promise = promise;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return promise.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return promise.isCancelled();
    }

    public boolean isDone() {
        return promise.isDone();
    }

    public AlgoResponse get() throws InterruptedException, ExecutionException {
        return promise.get();
    }

    public AlgoResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return promise.get(timeout, unit);
    }
}
