package com.algorithmia.algorithm;

import com.google.gson.JsonObject;

import java.io.PrintWriter;
import java.io.StringWriter;

class SerializableException<T extends RuntimeException> {
    String message;
    String stackTrace;
    String errorType;

    SerializableException(T e) {

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        message = e.getMessage();
        stackTrace = exceptionAsString;
        errorType = e.getClass().toString();
    }

    String getJsonOutput() {
        JsonObject node = new JsonObject();
        node.addProperty("message", this.message);
        node.addProperty("stack_trace", this.stackTrace);
        node.addProperty("error_type", this.errorType);
        return node.toString();
    }
}
