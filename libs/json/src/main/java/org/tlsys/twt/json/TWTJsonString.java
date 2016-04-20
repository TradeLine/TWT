package org.tlsys.twt.json;

import javax.json.JsonString;

/**
 * Created by Субочев Антон on 20.04.2016.
 */
public class TWTJsonString implements JsonString {

    private final String value;

    public TWTJsonString(String value) {
        this.value = value;
    }

    @Override
    public String getString() {
        return value;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.STRING;
    }
}
