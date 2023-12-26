package me.croshaw.carservicesimulation.simulation.core.util.adapters;

import com.google.gson.*;
import javafx.scene.paint.Color;

import java.lang.reflect.Type;
import java.time.Duration;

public class ColorTypeAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
    @Override
    public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return Color.valueOf(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(Color color, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(color.toString());
    }
}
