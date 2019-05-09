package com.algorithmia.algorithmHandler;


//taken from https://stackoverflow.com/questions/14242236/let-gson-throw-exceptions-on-wrong-types/29024682

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class BetterDeserialization<T> implements JsonDeserializer<T> {

    public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) {
        T candidate = new Gson().fromJson(je, type);

        Field[] fields = candidate.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Required.class) != null) {
                try {
                    field.setAccessible(true);
                    if (field.get(candidate) == null) {
                        throw new RuntimeException("Missing required field in JSON input: " + field.getName());
                    }
                } catch (IllegalArgumentException | IllegalStateException | IllegalAccessException ex) {
                    throw new RuntimeException("Something went wrong during deserialization", ex.getCause());
                }
            }
        }
        return candidate;
    }
}
