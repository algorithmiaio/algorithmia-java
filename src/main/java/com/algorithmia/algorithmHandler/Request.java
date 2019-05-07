package com.algorithmia.algorithmHandler;

import com.google.gson.JsonElement;

class Request {
    public String content_type;
    public JsonElement data;

    Request(String content_type, JsonElement data) {

        this.content_type = content_type;
        this.data = data;
    }
}
