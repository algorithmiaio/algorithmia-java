package com.algorithmia.algorithmHandler;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ReflectionHelper {


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

    public static <T, R> Optional<String> getMethodName(
            DebuggableFunction<T, R> methodReference) {
        Optional<SerializedLambda> lambda = getLambda(methodReference);
        return lambda.map(l -> l.getImplMethodName());
    }

    public static <T1, T2, R> Optional<String> getMethodName(DebuggableBifunction<T1, T2, R> methodReference) {
        Optional<SerializedLambda> lambda = getLambda(methodReference);
        return lambda.map(l -> l.getImplMethodName());
    }

    private static Optional<SerializedLambda> getLambda(Serializable lambda) {
        for (Class<?> cl = lambda.getClass(); cl != null; cl = cl.getSuperclass()) {
            try {
                Method m = cl.getDeclaredMethod("writeReplace");
                m.setAccessible(true);
                Object replacement = m.invoke(lambda);
                if (!(replacement instanceof SerializedLambda)) {
                    break; // custom interface implementation
                }
                SerializedLambda l = (SerializedLambda) replacement;
                return Optional.of(l);
            } catch (NoSuchMethodException e) {
                // do nothing
            } catch (IllegalAccessException | InvocationTargetException e) {
                break;
            }
        }

        return Optional.empty();
    }
}