package org.tlsys.lex;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.declare.DeclareVar;
import org.tlsys.lex.declare.VBlock;
import org.tlsys.lex.declare.VClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class Try extends Operation {

    private static final long serialVersionUID = 8100497016534329612L;
    public ArrayList<Catch> catchs = new ArrayList<>();
    public VBlock block;
    private Context parentContext;

    public Try() {
    }

    public Try(Context parentContext) {
        this.parentContext = Objects.requireNonNull(parentContext);
    }

    @Override
    public Optional<SVar> find(String name, Predicate<Context> searchIn) {
        if (searchIn.test(parentContext))
            return parentContext.find(name, searchIn);
        return Optional.empty();
    }

    @Override
    public void getUsing(Collect c) {
        c.add(block);
        for (Catch cc : catchs)
            c.add(cc);
    }

    public static class Catch implements Context, Using, Serializable {
        public ArrayList<VClass> classes = new ArrayList<>();
        public VBlock block;
        private Context parentContext;
        private DeclareVar declareVar;

        public DeclareVar getDeclareVar() {
            return declareVar;
        }
        
        public Catch(Context parentContext, DeclareVar declareVar) {
            this.parentContext = parentContext;
            this.declareVar = declareVar;
        }

        @Override
        public Optional<SVar> find(String name, Predicate<Context> searchIn) {
            if (searchIn.test(declareVar)) {
                Optional<SVar> o = declareVar.find(name, searchIn);
                if (o.isPresent())
                    return o;
            }

            if (searchIn.test(parentContext))
                return parentContext.find(name, searchIn);
            return Optional.empty();
        }

        @Override
        public void getUsing(Collect c) {
            for (VClass cc : classes)
                c.add(cc);
            c.add(block);
        }
    }
}
