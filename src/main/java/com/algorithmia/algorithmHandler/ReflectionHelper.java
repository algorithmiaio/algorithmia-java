package com.algorithmia.algorithmHandler;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ReflectionHelper {


    public static <T, R> String getMethodName(
            DebuggableFunction<T, R> methodReference) {
        SerializedLambda lambda = getLambda(methodReference);
        return lambda.getImplMethodName();
    }

    public static <T1, T2, R> String getMethodName(DebuggableBifunction<T1, T2, R> methodReference) {
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
                // do nothing
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