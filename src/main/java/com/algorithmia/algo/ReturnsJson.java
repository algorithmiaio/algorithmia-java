package com.algorithmia.algo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For algorithm development, indicates that a java method is returning a JSON-serialized String.
 * This will result in the API server returning a Content-Type of JSON.  If the String returned is
 * not valid JSON, then the call will result in a JSON parsing exception.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReturnsJson {}