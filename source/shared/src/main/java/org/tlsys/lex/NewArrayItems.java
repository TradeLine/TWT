package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.ArrayClass;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Антон on 17.01.2016.
 */
public class NewArrayItems extends Value implements HavinSourceStart {

    private static final long serialVersionUID = -6037079587556943990L;
    private final SourcePoint point;
    public ArrayList<Value> elements = new ArrayList<>();
    private ArrayClass clazz;

    public NewArrayItems(ArrayClass clazz, SourcePoint point) {
        this.clazz = clazz;
        this.point = point;
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

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);
        for (int i = 0; i < elements.size(); i++) {
            Optional<Value> op = ReplaceHelper.replace(elements.get(i), replaceControl);
            if (op.isPresent())
                elements.set(i, op.get());
        }
    }

    @Override
    public SourcePoint getStartPoint() {
        return point;
    }
}