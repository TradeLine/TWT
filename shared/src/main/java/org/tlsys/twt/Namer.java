package org.tlsys.twt;

import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Symbol;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface Namer {
    public String getClassName(Class clazz);
    public String getArgument(String name);
    public String getVarName(Symbol.VarSymbol var);
    public String getVarName(VariableTree var);
    public String getMethodName(Method method);
    public String getInitName(Constructor method);
    public String getConstructor(Constructor method);
    CompileContext getCompileContext();
}
