package javax.json;

import java.util.Map;

public interface JsonObject extends JsonStructure, Map<String, JsonValue> {
    boolean getBoolean(String name);

    boolean getBoolean(String name, boolean defaultValue);

    int getInt(String name);

    int getInt(String name, int defaultValue);

    JsonArray getJsonArray(String name);

    JsonNumber getJsonNumber(String name);

    JsonObject getJsonObject(String name);

    JsonString getJsonString(String name);

    String getString(String name);

    String getString(String name, String defaultValue);

    boolean isNull(String name);
}
