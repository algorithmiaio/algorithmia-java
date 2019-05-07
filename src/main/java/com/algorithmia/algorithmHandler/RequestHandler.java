package com.algorithmia.algorithmHandler;
import java.util.Scanner;

import com.algorithmia.TypeToken;
import com.google.gson.*;
import org.apache.commons.codec.binary.Base64;


class RequestHandler<ALGO_INPUT>
{

    private Scanner input;
    private JsonParser parser;
    private Gson gson;
    private Class<ALGO_INPUT> inputClass;

    RequestHandler(Class<ALGO_INPUT> inputClass){
        this.input = new Scanner(System.in);
        this.parser = new JsonParser();
        this.gson = new Gson();
        this.inputClass = inputClass;
    }


    private  ALGO_INPUT ProcessRequest(Request request) throws Exception{
        try {
            if (inputClass == byte[].class) {
                return (ALGO_INPUT) Base64.decodeBase64((request.data.getAsString()));
            } else if (inputClass == JsonElement.class) {
                return (ALGO_INPUT) inputClass;
            } else if (inputClass == String.class) {
                return (ALGO_INPUT) request.data.getAsString();
            } else if (inputClass == Number.class) {
                return (ALGO_INPUT) request.data.getAsNumber();
            } else {
                return gson.fromJson(request.data, inputClass);
            }
        }
        catch (Exception e) {
            throw new Exception("unable to parse input into type " + inputClass.getName() + " , with input " + request.data.getAsString());
        }

    }


     ALGO_INPUT GetNextRequest() throws Exception{
        String line = null;
        try {
            if (input.hasNextLine()) {
                line = input.nextLine();
                JsonObject json = parser.parse(line).getAsJsonObject();
                String contentType = json.get("content_type").getAsString();
                JsonElement data = json.get("data");
                Request request = new Request(contentType, data);
                ALGO_INPUT result = ProcessRequest(request);
                return result;
            } else {
                return null;
            }
        } catch (JsonSyntaxException e){
            throw new Exception("unable to parse the request" + line  + "as valid json");
        }
    }
}
