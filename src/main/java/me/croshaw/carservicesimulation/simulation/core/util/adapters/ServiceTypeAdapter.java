package me.croshaw.carservicesimulation.simulation.core.util.adapters;

import com.google.gson.*;
import javafx.scene.paint.Color;
import me.croshaw.carservicesimulation.simulation.core.service.Service;
import me.croshaw.carservicesimulation.simulation.core.util.ValueRange;

import java.lang.reflect.Type;
import java.time.Duration;

public class ServiceTypeAdapter implements JsonSerializer<Service>, JsonDeserializer<Service> {

    @Override
    public Service deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var jo = jsonElement.getAsJsonObject();
        Duration dur = Duration.parse(jo.get("duration").getAsString());
        long min = jo.get("range").getAsJsonObject().get("min").getAsLong();
        long max = jo.get("range").getAsJsonObject().get("max").getAsLong();
        return new Service(jo.get("name").getAsString(), dur, new ValueRange<>(min, max), jo.get("price").getAsDouble());
    }

    @Override
    public JsonElement serialize(Service service, Type type, JsonSerializationContext jsonSerializationContext) {
        String name = service.getName();
        double price = service.getPrice();
        Duration dur = service.getAverageDurationOfService();
        var t = service.getOffsetServicingRange();
        JsonObject js = new JsonObject();
        js.add("name", new JsonPrimitive(name));
        js.add("price", new JsonPrimitive(price));
        js.add("duration", new JsonPrimitive(dur.toString()));
        JsonObject jst = new JsonObject();
        jst.add("min", new JsonPrimitive(t.getMin()));
        jst.add("max", new JsonPrimitive(t.getMax()));
        js.add("range", jst);
        return js;
    }
}