package algorithmia.client;

import algorithmia.algo.AlgoResponse;
import algorithmia.algo.AlgoSuccess;
import algorithmia.algo.AlgoFailure;
import algorithmia.algo.Metadata;
import algorithmia.AlgorithmException;
import algorithmia.APIException;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ContentTooLongException;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.entity.ContentBufferEntity;
import org.apache.http.util.Asserts;

/**
 * Various HTTP actions, using our HttpClient class, and automatically adding authorization
 */
public class HttpClientHelpers {
    private HttpClientHelpers() {}  // non-instantiable

    static abstract public class AbstractBasicResponseConsumer<T> extends AbstractAsyncResponseConsumer<T> {
        protected volatile HttpResponse response;
        private volatile SimpleInputBuffer buf;

        @Override
        protected void onResponseReceived(final HttpResponse response) throws IOException {
            this.response = response;
        }

        @Override
        protected void onEntityEnclosed(final HttpEntity entity, final ContentType contentType)
                throws IOException {
            long len = entity.getContentLength();
            if (len > Integer.MAX_VALUE) {
                throw new ContentTooLongException("Entity content is too long: " + len);
            }
            if (len < 0) {
                len = 4096;
            }
            this.buf = new SimpleInputBuffer((int) len, new HeapByteBufferAllocator());
            this.response.setEntity(new ContentBufferEntity(entity, this.buf));
        }

        @Override
        protected void onContentReceived(final ContentDecoder decoder, final IOControl ioctrl)
                throws IOException {
            Asserts.notNull(this.buf, "Content buffer");
            this.buf.consumeContent(decoder);
        }

        @Override
        protected void releaseResources() {
            this.response = null;
            this.buf = null;
        }
    }

//    static public class JsonResponseHandler extends AbstractBasicResponseConsumer<JsonElement> {
//        @Override
//        protected JsonElement buildResult(HttpContext context) throws APIException {
//            return parseResponseJson(response);
//        }
//    }

    static public class JsonDeserializeResponseHandler<T> extends AbstractBasicResponseConsumer<T> {
        final private TypeToken typeToken;
        public JsonDeserializeResponseHandler(TypeToken typeToken) {
            this.typeToken = typeToken;
        }
        @Override
        protected T buildResult(HttpContext context) throws APIException {
            JsonElement json = parseResponseJson(response);
            Gson gson = new Gson();
            return gson.fromJson(json, typeToken.getType());
        }
    }

    static public class AlgoResponseHandler extends AbstractBasicResponseConsumer<AlgoResponse> {
        @Override
        protected AlgoResponse buildResult(HttpContext context) throws APIException {
            JsonElement json = parseResponseJson(response);
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


    public static void assertStatusSuccess(HttpResponse response) throws APIException {
        final int status = response.getStatusLine().getStatusCode();
        if(200 > status || status > 300) {
            throw APIException.fromHttpResponse(response, null);  //TODO: stop sending null URL
        }
    }

    // public static void assertNonError(JsonElement json) throws APIException {
    //     if(json != null && json.isJsonObject()) {
    //         final JsonObject obj = json.getAsJsonObject();
    //         if(obj.has("error")) {
    //             final JsonObject error = obj.getAsJsonObject("error");
    //             final String msg = error.get("message").getAsString();
    //             final String stacktrace = error.get("stacktrace").getAsString();
    //             throw new APIException(msg);
    //         }
    //     }
    // }

    public static JsonElement parseResponseJson(HttpResponse response) throws APIException {
        assertStatusSuccess(response);

        try {
            final HttpEntity entity = response.getEntity();
            final InputStream is = entity.getContent();
            final JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(new InputStreamReader(is));
            // assertNonError(json);
            return json;

        } catch(IOException ex) {
            throw new APIException("IOException: " + ex.getMessage());
        }
    }

}
