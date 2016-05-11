package org.tlsys.twt;

import org.tlsys.lex.Value;
import org.tlsys.lex.declare.Member;
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VField;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AnnonimuRecord {
    private final Member parent;
    private final VClass clazz;
    private final HashMap<VField, Value> values = new HashMap<>();

    public AnnonimuRecord(Member parent, VClass clazz) {
        this.parent = parent;
        this.clazz = clazz;
    }

    public Member getParent() {
        return parent;
    }

    public VClass getClazz() {
        return clazz;
    }

    public HashMap<VField, Value> getValues() {
        return values;
    }

    public AnnonimuRecord addValue(VField field, Value value) {
        values.put(field, value);
        return this;
    }

    public Optional<Value> getValue(VField field) {
        return Optional.ofNullable(values.get(field));
    }

    public Optional<Value> getValue(String name) {
        for (Map.Entry<VField, Value> e : values.entrySet()) {
            if (name.equals(e.getKey().getRealName()) || name.equals(e.getKey().getAliasName()))
                return Optional.of(e.getValue());
        }
        return Optional.empty();
    }
}
