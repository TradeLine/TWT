package org.tlsys.lex.declare;

import org.tlsys.lex.Collect;
import org.tlsys.lex.Context;
import org.tlsys.sourcemap.SourcePoint;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.function.Predicate;

public class StaticBlock implements Context,Member,CodeDynLoad {

    private static final long serialVersionUID = 9204023692970447528L;
    private VBlock block;

    private VClass parent;


    public StaticBlock(VClass parent, SourcePoint startPoint, SourcePoint endPoint) {
        this.parent = parent;
        block = new VBlock(this, startPoint, endPoint);
    }

    public VBlock getBlock() {
        return block;
    }

    @Override
    public void saveCode(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeObject(block);
    }

    @Override
    public void loadCode(ObjectInputStream outputStream) throws IOException, ClassNotFoundException {
        block = (VBlock) outputStream.readObject();
    }

    @Override
    public int getModificators() {
        return 0;
    }

    @Override
    public boolean isThis(String name) {
        return false;
    }

    @Override
    public VClass getParent() {
        return null;
    }

    @Override
    public void getUsing(Collect c) {

    }

    @Override
    public Optional<Context> find(String name, Predicate<Context> searchIn) {
        return parent.find(name, searchIn);
    }
}
