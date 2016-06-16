package org.tlsys;

public interface BytecodeTransformer
{
    byte[] transform(String className, byte[] bytecode);
    boolean requiresTransformation(String className);
}
