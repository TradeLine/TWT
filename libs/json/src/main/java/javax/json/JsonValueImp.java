package javax.json;

/**
 * Created by Субочев Антон on 20.04.2016.
 */
public class JsonValueImp implements JsonValue {
    @Override
    public ValueType getValueType() {
        return ValueType.OBJECT;
    }
}
