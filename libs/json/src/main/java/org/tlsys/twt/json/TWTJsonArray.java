package org.tlsys.twt.json;

import javax.json.JsonArray;
import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.List;

public class TWTJsonArray extends ArrayList<JsonValue> implements JsonArray {

    @Override
    public boolean getBoolean(int index) {
        return false;
    }

    @Override
    public boolean getBoolean(int index, boolean defaultValue) {
        return false;
    }

    @Override
    public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
        return null;
    }
}
