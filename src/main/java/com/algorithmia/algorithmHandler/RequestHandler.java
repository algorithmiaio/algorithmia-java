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

        try{
            return gson.fromJson(request.data, inputClass);
        } catch (Exception a) {
            try{
            return (ALGO_INPUT) request.data;
        } catch (Exception b) {
                try {
                    return (ALGO_INPUT) Base64.decodeBase64(request.data.getAsString());
                } catch (Exception c) {
                    try {
                        return (ALGO_INPUT) request.data.getAsString();
                    } catch (Exception l){
                        throw new Exception("We tried all matches, input doesn't satisfy any acceptable type");
                    }
                }
            }
        }
    }


     ALGO_INPUT GetNextRequest() throws Exception{
        if(input.hasNextLine()){
            String line = input.nextLine();
            JsonObject json = parser.parse(line).getAsJsonObject();
            String contentType = json.get("content_type").getAsString();
            JsonElement data = json.get("data");
            Request request = new Request(contentType, data);
            ALGO_INPUT result = ProcessRequest(request);
            return result;
        }
        else {
            return null;
        }
    }
}
