package com.algorithmia.algo;

import com.google.gson.annotations.SerializedName;

public enum ContentType {
    @SerializedName("void") Void("void"),
    @SerializedName("text") Text("text"),
    @SerializedName("json") Json("json"),
    @SerializedName("binary") Binary("binary");

    public String name;

    ContentType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static ContentType fromString(String name) {
        if(Void.name.equals(name)) {
            return Void;
        } else if(Text.name.equals(name)) {
            return Text;
        } else if(Json.name.equals(name)) {
            return Json;
        } else if(Binary.name.equals(name)) {
            return Binary;
        } else {
            return Void;
        }
    }

}
