package com.algorithmia.algorithm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation when you are expecting a java class as input, and have fields that are required.
 * If you do not annotate a field with this annotation, it will be considered optional, and if missing - will simply be set to null.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Required {
}