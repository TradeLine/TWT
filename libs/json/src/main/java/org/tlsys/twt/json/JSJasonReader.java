package org.tlsys.twt.json;

import org.tlsys.twt.Script;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import java.io.Closeable;

public class JSJasonReader implements javax.json.JsonReader, Closeable {
    private Object jsObject;

    public JSJasonReader(String json) {
        jsObject = Script.code("JSON.parse(", json, ")");
    }

    @Override
    public void close() {
        jsObject = null;
    }

    @Override
    public JsonStructure read() {
        if (jsObject == null)
            throw new IllegalStateException("Reader was closed");
        throw new RuntimeException("Not supported yet");
    }

    @Override
    public JsonArray readArray() {
        if (jsObject == null)
            throw new IllegalStateException("Reader was closed");
        throw new RuntimeException("Not supported yet");
    }

    @Override
    public JsonObject readObject() {
        if (jsObject == null)
            throw new IllegalStateException("Reader was closed");
        throw new RuntimeException("Not supported yet");
    }
}
