package com.algorithmia.algo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For algorithm development, labels an load method indicating it
 * should be passed in the raw/unparsed JSON input from the user.  If the user did not call
 * the algorithm with a JSON content-type, this load method will not be called.
 * <b>Note: You can only have a single load method if you use @AcceptsJson</b>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AcceptsJson {}
