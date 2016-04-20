package javax.json;

public interface JsonReader {

    void close();

    JsonStructure read();

    JsonArray readArray();

    JsonObject readObject();
}
