package com.algorithmia;

import java.io.IOException;

import java.io.InputStream;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.Header;

/**
 * APIException indicates a problem communicating with Algorithmia
 */
public class APIException extends IOException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new API exception with the specified detail message, no stack trace.
     * @param message the detail message
     */
    public APIException(String message) {
        super(message);
    }

    /**
     * Constructs a new API exception with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause of the AlgorithmException
     */
    public APIException(String message, Throwable cause) {
        super(message, cause);
    }

    public static APIException fromHttpResponse(HttpResponse response) {
        final int status = response.getStatusLine().getStatusCode();
        final HttpEntity entity = response.getEntity();
        final Header errorHeader = response.getFirstHeader("X-Error-Message");

        String errorMessage = "";
        if(entity != null) {
            try {
                final InputStream is = entity.getContent();
                errorMessage = ": " + IOUtils.toString(is, Charsets.UTF_8);
            } catch(IOException e) {
                errorMessage = ": IOException reading response: " + e.getMessage();
            }
        } else if(errorHeader != null) {
            errorMessage = ": " + errorHeader.getValue();
        }

        if(status == 400) {
            return new APIException("400 malformed request" + errorMessage);
        } else if(status == 401) {
            return new APIException("401 not authorized" + errorMessage);
        } else if(status == 404) {
            return new APIException("404 not found" + errorMessage);
        } else if(status == 415) {
            return new APIException("415 unsupported content type" + errorMessage);
        } else if(status == 504) {
            return new APIException("504 server timeout" + errorMessage);
        } else {
            return new APIException(status + " unexpected API response" + errorMessage);
        }
    }

}
