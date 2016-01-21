package org.tlsys.twt;

import java.io.PrintStream;
import java.lang.reflect.Constructor;

public interface ConstructorGenerator {
    public void constructorGenerator(GenContext context, Constructor constructor, String[] arguments, PrintStream stream) throws ClassNotFoundException;
}
