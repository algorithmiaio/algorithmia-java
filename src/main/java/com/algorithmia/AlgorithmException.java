package com.algorithmia;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * AlgorithmExceptions indicate an problem when running an algorithm.
 * AlgorithmExceptions will often be used as a wrapper for other Exceptions
 */
public class AlgorithmException extends Exception {
    private static final long serialVersionUID = 1L;

    public final String stacktrace;

    /**
     * Constructs a new algorithm exception with the specified detail message, no stack trace.
     * @param message the detail message
     */
    public AlgorithmException(String message) {
        super(message);
        stacktrace = null;
        this.setStackTrace(new StackTraceElement[]{});
    }

    /**
     * Constructs a new algorithm exception with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause of the AlgorithmException
     */
    public AlgorithmException(String message, Throwable cause) {
        super(message, wrapException(cause));
        // Copy stack trace from this
        stacktrace = wrapStackTrace(this);
        if(cause != null) {
            this.setStackTrace(cause.getStackTrace());
        }
    }

    /**
     * Constructs a new algorithm exception from an existing exception
     * @param throwable the original Exception to wrap in an AlgorithmException
     */
    public AlgorithmException(Throwable throwable) {
        this(
            makeMessage(throwable),
            wrapException(throwable == null? null : throwable.getCause()),
            wrapStackTrace(throwable)
        );
        if(throwable != null) {
            this.setStackTrace(throwable.getStackTrace());
        }
    }
    /** Helper method to construct "Exception: Message" */
    private static String makeMessage(Throwable throwable) {
        if(throwable instanceof AlgorithmException) {
            return throwable.getMessage();
        } else {
            final String className = throwable.getClass().getName();
            final String message = throwable.getMessage();
            return className + ": " + message;
        }
    }

    /**
     * Constructs a new algorithm exception from an exception message and stacktrace
     * @param message the detail message
     * @param cause the cause of the AlgorithmException
     * @param stacktrace the stack trace that caused this exception
     */
    public AlgorithmException(String message, AlgorithmException cause, String stacktrace) {
        super(message, cause);
        this.stacktrace = stacktrace;
        if(cause != null) {
            this.setStackTrace(cause.getStackTrace());
        } else {
            this.setStackTrace(new StackTraceElement[]{});
        }
    }

    @Override
    public String toString() {
        final String cause = getCause() == null ? "" : " (" + getCause().getMessage() + ")";
        return getMessage() + cause;
    }

    /**
     * Constructs a new algorithm exception from an existing throwable.
     * This replaces all Exceptions in the cause hierarchy to be replaced with AlgorithmException, for inter-jvm communication safety.
     */
    private static AlgorithmException wrapException(Throwable throwable) {
        if(throwable == null) {
            return null;
        } else if(throwable instanceof AlgorithmException) {
            return (AlgorithmException) throwable;
        } else {
            return new AlgorithmException(throwable.getMessage(), wrapException(throwable.getCause()), wrapStackTrace(throwable));
        }
    }

    private static String wrapStackTrace(Throwable throwable) {
        if(throwable == null) {
            return null;
        } else if(throwable instanceof AlgorithmException) {
            return ((AlgorithmException) throwable).stacktrace;
        } else {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }

}
