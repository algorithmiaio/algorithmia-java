package com.algorithmia;

/**
 * TypeToken used for converting JSON into specific types
 * <p>
 * To avoid type erasure, be sure to instantiate the TypeToken
 * as an anonymous class. Example:
 * <pre>
 * {@code
 *     new TypeToken<MyClass>(){}
 * }
 * </pre>
 */

public class TypeToken<T> extends com.google.gson.reflect.TypeToken<T> {
    protected TypeToken() {
        super();
    }
}