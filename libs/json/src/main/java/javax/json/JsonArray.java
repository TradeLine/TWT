package javax.json;

import java.util.List;

public interface JsonArray extends JsonStructure, List<JsonValue> {

    boolean getBoolean(int index);

    boolean getBoolean(int index, boolean defaultValue);

    default int getInt(int index) {
        return getJsonNumber(index).intValue();
    }

    default int getInt(int index, int defaultValue) {
        if (index >= size() || get(index).getValueType() != ValueType.NUMBER)
            return defaultValue;
        return getInt(index);
    }

    default JsonArray getJsonArray(int index) {
        if (get(index) instanceof JsonArray)
            return (JsonArray) get(index);
        throw new IllegalArgumentException();
    }

    default JsonNumber getJsonNumber(int index) {
        if (get(index) instanceof JsonNumber)
            return (JsonNumber) get(index);
        throw new IllegalArgumentException();
    }

    default JsonObject getJsonObject(int index) {
        if (get(index) instanceof JsonObject)
            return (JsonObject) get(index);
        throw new IllegalArgumentException();
    }

    default JsonString getJsonString(int index) {
        if (get(index) instanceof JsonString)
            return (JsonString) get(index);
        throw new IllegalArgumentException();
    }

    default String getString(int index) {
        return getJsonString(index).getString();
    }

    default String getString(int index, String defaultValue) {
        if (index >= size() || get(index).getValueType() != ValueType.STRING)
            return defaultValue;
        return getString(index);
    }

    <T extends JsonValue> List<T> getValuesAs(Class<T> clazz);

    default boolean isNull(int index) {
        return get(index).getValueType() == ValueType.NULL;
    }

    @Override
    default ValueType getValueType() {
        return ValueType.ARRAY;
    }
}
