package com.algorithmia.algorithmHandler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Base64;

class Response<T> {
    private MetaData metaData;
    private T result;
    private Gson gson = new Gson();

    public class MetaData{
        private String content_type;
        MetaData(String contentType){this.content_type = contentType;}
    }
    Response(Object rawData){
        String contentType;
        T data;
        if(rawData == null){
            contentType = "json";
            data = null;
        }
        else if(rawData instanceof String){
            contentType = "text";
            data = (T)rawData;
        }
        else if (rawData instanceof byte[]){
            contentType = "binary";
            data = (T)Base64.encodeBase64String((byte[])rawData);

        } else {
            contentType = "json";
            data = (T)rawData;
        }

        metaData = new MetaData(contentType);
        result = data;
    }
    String getJsonOutput(){
        JsonObject node = new JsonObject();
        JsonObject metaData = new JsonObject();
        metaData.addProperty("content_type", this.metaData.content_type);
        node.add("metadata", metaData);
        node.add("result", gson.toJsonTree(this.result));
        return node.toString();
    }
}
