package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Антон on 17.01.2016.
 */
public class NewArrayItems extends Value {

    private static final long serialVersionUID = -6037079587556943990L;
    private ArrayClass clazz;
    public ArrayList<Value> elements = new ArrayList<>();

    public NewArrayItems() {
    }

    public NewArrayItems(ArrayClass clazz) {
        this.clazz = clazz;
    }

    @Override
    public VClass getType() {
        return clazz;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        for (Value v : elements) {
            c.add(v);
        }
        c.add(clazz);
    }

    public NewArrayItems addEl(Value value) {
        elements.add(value);
        return this;
    }
}