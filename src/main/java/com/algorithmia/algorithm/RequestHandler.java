package com.algorithmia.algorithm;

import com.google.gson.*;
import org.apache.commons.codec.binary.Base64;

import java.util.Scanner;


class RequestHandler<ALGO_INPUT> {

    private Scanner input = new Scanner(System.in);
    private JsonParser parser = new JsonParser();
    private Gson gson;
    private Class<ALGO_INPUT> inputClass;

    RequestHandler(Class<ALGO_INPUT> inputClass) {

        this.inputClass = inputClass;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(inputClass, new BetterDeserialization<>())
                .create();
    }


    private ALGO_INPUT processRequest(Request request) {
        try {
            if (inputClass == byte[].class) {
                return inputClass.cast(Base64.decodeBase64((request.data.getAsString())));
            } else if (inputClass == JsonElement.class) {
                return inputClass.cast(request.data);
            } else if (inputClass == String.class) {
                return inputClass.cast(request.data.getAsString());
            } else if (inputClass == Number.class) {
                return inputClass.cast(request.data.getAsNumber());
            } else {
                return gson.fromJson(request.data, inputClass);
            }
        } catch (ClassCastException | IllegalStateException ex) {
            String className = inputClass.getName();
            String req = request.data.toString();
            throw new RuntimeException("unable to parse input into type " + className + " , with input " + req);
        }
    }


    ALGO_INPUT getNextRequest() {
        String line = null;
        try {
            ALGO_INPUT result;
            if (input.hasNextLine()) {
                line = input.nextLine();
                JsonObject json = parser.parse(line).getAsJsonObject();
                String contentType = json.get("content_type").getAsString();
                JsonElement data = json.get("data");
                Request request = new Request(contentType, data);
                result = processRequest(request);
                return result;
            } else {
                return null;
            }
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("unable to parse the request" + line + "as valid json");
        }
    }
}
