package org.tlsys.twt.json;

import javax.json.JsonNumber;

/**
 * Created by Субочев Антон on 20.04.2016.
 */
public class TWTJsonNumber implements JsonNumber {

    private final double value;

    public TWTJsonNumber(double value) {
        this.value = value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public int intValue() {
        return (int) longValue();
    }

    @Override
    public int intValueExact() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public boolean isIntegral() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public long longValue() {
        return (int) Math.round(value);
    }

    @Override
    public long longValueExact() {
        throw new RuntimeException("Not supported");
    }

    @Override
    public ValueType getValueType() {
        return ValueType.NUMBER;
    }
}
