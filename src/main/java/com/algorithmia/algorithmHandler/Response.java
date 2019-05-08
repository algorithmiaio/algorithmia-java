package com.algorithmia.algorithmHandler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Base64;

class Response {
    private MetaData metaData;
    private JsonElement result;
    private Gson gson = new Gson();

    public class MetaData {
        private String content_type;

        MetaData(String contentType) {
            this.content_type = contentType;
        }
    }


    <OUTPUT> Response(OUTPUT data) {
        String contentType;
        JsonElement jsonData;
        try {
            if (data.getClass() == null) {
                contentType = "json";
                jsonData = null;
            } else if (data.getClass() == String.class) {
                contentType = "text";
                jsonData = gson.toJsonTree(data);
            } else if (data.getClass() == byte.class) {
                contentType = "binary";
                jsonData = gson.toJsonTree(Base64.encodeBase64String((byte[]) data));

            } else {
                contentType = "json";
                jsonData = gson.toJsonTree(data);
            }

            metaData = new MetaData(contentType);
            result = jsonData;
        } catch (StackOverflowError e) {
            throw new RuntimeException("your output type was not successfully serializable.", e);
        }
    }

    String getJsonOutput() {
        JsonObject node = new JsonObject();
        JsonObject metaData = new JsonObject();
        metaData.addProperty("content_type", this.metaData.content_type);
        node.add("metadata", metaData);
        node.add("result", gson.toJsonTree(this.result));
        return node.toString();
    }
}
