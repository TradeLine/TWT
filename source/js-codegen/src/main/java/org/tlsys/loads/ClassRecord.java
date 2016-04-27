package org.tlsys.loads;

public class ClassRecord {
    private final JArray<MethodRecord> methods = new JArray<>();

    public ClassRecord addMethod(MethodRecord record) {
        methods.add(record);
        return this;
    }
}
