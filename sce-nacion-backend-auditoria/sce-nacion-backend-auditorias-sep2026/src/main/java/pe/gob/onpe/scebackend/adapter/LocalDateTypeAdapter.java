package pe.gob.onpe.scebackend.adapter;

import com.google.gson.*;
import pe.gob.onpe.scebackend.utils.DateTimeUtil;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class LocalDateTypeAdapter  implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime>{

    DateTimeFormatter formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
            .append(DateTimeFormatter.ofPattern("yyyy-MMM-dd")).toFormatter();


    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localDateTime.format(formatter));
    }


    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return DateTimeUtil.convertISO8601StringToLocalDateTime(jsonElement.getAsString(), SceConstantes.TIMEZONE);
    }
}
