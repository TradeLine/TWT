package org.tlsys.lex;

import org.tlsys.HavinSourceStart;
import org.tlsys.ReplaceHelper;
import org.tlsys.ReplaceVisiter;
import org.tlsys.lex.declare.DeclareVar;
import org.tlsys.lex.declare.VBlock;
import org.tlsys.lex.declare.VClass;
import org.tlsys.sourcemap.SourcePoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class Try extends Operation implements HavinSourceStart {

    private static final long serialVersionUID = 8100497016534329612L;
    public ArrayList<Catch> catchs = new ArrayList<>();
    public VBlock block;
    private Context parentContext;
    private final SourcePoint point;

    public Try(Context parentContext, SourcePoint point) {
        this.parentContext = Objects.requireNonNull(parentContext);
        this.point = point;
    }

    @Override
    public SourcePoint getPoint() {
        return point;
    }

    public Context getParentContext() {
        return parentContext;
    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
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

    @Override
    public void visit(ReplaceVisiter replaceControl) {
        super.visit(replaceControl);

        ReplaceHelper.replace(block, replaceControl).ifPresent(e -> block = e);
        for (int i = 0; i < catchs.size(); i++) {
            Optional<Catch> c = ReplaceHelper.replace(catchs.get(i), replaceControl);
            if (c.isPresent())
                catchs.set(i, c.get());
        }
    }

    public static class Catch extends Operation implements Context, Using, Serializable, HavinSourceStart {
        private static final long serialVersionUID = 3242922465596941371L;
        public ArrayList<VClass> classes = new ArrayList<>();
        public VBlock block;
        private Context parentContext;
        private DeclareVar declareVar;
        private final SourcePoint point;

        public Catch(Context parentContext, DeclareVar declareVar, SourcePoint point) {
            this.parentContext = parentContext;
            this.declareVar = declareVar;
            this.point = point;
        }

        @Override
        public SourcePoint getPoint() {
            return point;
        }

        public DeclareVar getDeclareVar() {
            return declareVar;
        }

        public Context getParentContext() {
            return parentContext;
        }

        @Override
        public Optional<Context> find(String name, Predicate<Context> searchIn) {
            if (searchIn.test(declareVar)) {
                Optional<Context> o = declareVar.find(name, searchIn);
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
