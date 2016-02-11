package org.tlsys.lex.declare;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface CodeDynLoad {
    void saveCode(ObjectOutputStream outputStream) throws IOException;
    void loadCode(ObjectInputStream outputStream) throws IOException, ClassNotFoundException;
}
