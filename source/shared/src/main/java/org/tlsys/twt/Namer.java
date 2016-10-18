package org.tlsys.twt;

import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Symbol;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface Namer {
    String getClassName(Class clazz);
    String getArgument(String name);
    String getVarName(Symbol.VarSymbol var);
    String getVarName(VariableTree var);
    String getMethodName(Method method);
    String getInitName(Constructor method);
    String getConstructor(Constructor method);
    CompileContext getCompileContext();
}
