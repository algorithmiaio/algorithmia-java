package com.algorithmia.algorithm;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This static class and lambda interface to allow for the recovery of method names & original class names for method references.
 * Normally this information is lost due to erasure, but by enforcing the interface to be Serializable, we can recover the name and class, which allows us to perform reflection
 * on the primary classes apply method.
 */
// https://stackoverflow.com/questions/31178103/how-can-i-find-the-target-of-a-java8-method-reference
// https://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html

public class ReflectionHelper {


    static <T, R> String getMethodName(
            DebuggableFunction<T, R> methodReference) {
        SerializedLambda lambda = getLambda(methodReference);
        return lambda.getImplMethodName();
    }

    static <T1, T2, R> String getMethodName(DebuggableBifunction<T1, T2, R> methodReference) {
        SerializedLambda lambda = getLambda(methodReference);
        return lambda.getImplMethodName();
    }

    private static SerializedLambda getLambda(Serializable lambda) {
        for (Class<?> cl = lambda.getClass(); cl != null; cl = cl.getSuperclass()) {
            try {
                Method m = cl.getDeclaredMethod("writeReplace");
                m.setAccessible(true);
                Object replacement = m.invoke(lambda);
                if (!(replacement instanceof SerializedLambda)) {
                    break; // custom interface implementation
                }
                SerializedLambda l = (SerializedLambda) replacement;
                return l;
            } catch (NoSuchMethodException e) {
                // In the event that we're provided with a hierarchical class structure, our serializable method might not be found in the final class, it may be in a ancestor.
                // If the current scope does not implement serializable, we continue to increase scope until it does, or we exhaust superclasses.
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("The writeReplace method implemented by your algorithm class is incompatible with this framework", e);
            }
        }

        throw new RuntimeException("We were unable to find a Serializable form of your method reference. Please ensure that you're using a compatible version of java.");
    }

    @FunctionalInterface
    public interface DebuggableFunction<T, R> extends
            Serializable,
            Function<T, R> {
    }

    @FunctionalInterface
    public interface DebuggableBifunction<T1, T2, R> extends
            Serializable,
            BiFunction<T1, T2, R> {
    }
}