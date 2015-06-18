package com.algorithmia.util;

import com.algorithmia.AlgorithmException;
import com.algorithmia.APIException;
import com.algorithmia.algo.AlgoFailure;
import com.algorithmia.algo.AlgoResponse;
import com.algorithmia.algo.AlgoSuccess;
import com.algorithmia.algo.Metadata;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Various HTTP actions, using our HttpClient class, and automatically adding authorization
 */
public class JsonHelpers {
    private JsonHelpers() {}  // non-instantiable

    /**
    * Parse JSON RPC style result
     * @param json response that will be deserialized into an AlgoResponse
     * @return algorithm result (AlgoSuccess or AlgoFailure)
     * @throws APIException if there is a problem communication with the Algorithmia API.
    */
    public static AlgoResponse jsonToAlgoResponse(JsonElement json) throws APIException {
        if(json != null && json.isJsonObject()) {
            final JsonObject obj = json.getAsJsonObject();
            if(obj.has("error")) {
                final JsonObject error = obj.getAsJsonObject("error");
                final String msg = error.get("message").getAsString();
                final String stacktrace = error.get("stacktrace").getAsString();
                return new AlgoFailure(new AlgorithmException(msg, null, stacktrace));
            } else {
                JsonObject metaJson = obj.getAsJsonObject("metadata");
                Double duration = metaJson.get("duration").getAsDouble();
                JsonElement stdoutJson = metaJson.get("stdout");
                String stdout = (stdoutJson == null) ? null : stdoutJson.getAsString();
                Metadata meta = new Metadata(duration, stdout);
                return new AlgoSuccess(obj.get("result"), meta);
            }
        } else {
            throw new APIException("Unexpected API response: " + json);
        }
    }

}
