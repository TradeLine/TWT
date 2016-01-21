package org.tlsys.lex.declare;

import org.tlsys.lex.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StaticBlock extends VBlock implements CodeDynLoad {

    private static final long serialVersionUID = 9204023692970447528L;

    public StaticBlock() {
    }

    public StaticBlock(Context parentContext) {
        super(parentContext);
    }

    @Override
    public void saveCode(ObjectOutputStream outputStream) throws IOException {

    }

    @Override
    public void loadCode(ObjectInputStream outputStream) throws IOException, ClassNotFoundException {

    }
}
