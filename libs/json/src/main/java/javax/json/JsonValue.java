package javax.json;

public interface JsonValue {
    public static final JsonValue NULL = new JsonValueImp();
    public static final JsonValue TRUE = new JsonValueImp();
    public static final JsonValue FALSE = new JsonValueImp();

    ValueType getValueType();

    String toString();

    public enum ValueType {
        ARRAY,
        FALSE,
        NULL,
        NUMBER,
        OBJECT,
        STRING,
        TRUE
    }
}
