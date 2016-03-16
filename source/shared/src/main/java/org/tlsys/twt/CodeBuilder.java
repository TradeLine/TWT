package org.tlsys.twt;

import com.sun.tools.javac.tree.JCTree;
import org.tlsys.twt.desc.ClassDesc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.Collection;

public interface CodeBuilder {
    String arraySets(Class clazz, String ... args);
    String arrayInit(String classObj, String ... length);
    String invoke(Executable method, String ... arguments);
    String invoke(Executable method, JCTree.JCExpression ... arguments) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException;
    String initFor(Class clazz, String self);
    String invoke(String self, Executable method, String ... arguments);
    String invoke(JCTree.JCExpression self, Executable method, JCTree.JCExpression... arguments) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException;
    String invokeEx(Class currentClass, String self, Executable method, String ... arguments);
    String exp(JCTree.JCExpression scope, JCTree.JCExpression expression) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException;
    String cast(Class from, Class to, JCTree.JCExpression value) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException;
    public default String exp(JCTree.JCExpression expression) throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        return exp(null, expression);
    }
    String initValueFor(Class clazz, JCTree.JCExpression init) throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException;
    String newInst(Class clazz, Constructor constructor, String ... arguments);
    default String newInst(Class clazz, Constructor constructor, JCTree.JCExpression ... arguments) throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        return newInst(clazz, constructor, Arrays.asList(arguments));
    }

    default String newInst(Class clazz, Constructor constructor, Collection<JCTree.JCExpression> arguments) throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        String[] list = new String[arguments.size()];
        int c = 0;
        for (JCTree.JCExpression e : arguments)
            list[c++] = exp(null, e);
        return newInst(clazz, constructor, list);
    }
    String instenceOf(String ins, Class clazz) throws NoSuchMethodException, ClassNotFoundException;
    String getClass(Class clazz);
    String newAnnonimusInst(ClassDesc desc, Constructor constructor, Collection<JCTree.JCExpression> arguments) throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException;
    String getOrCreateClass(ClassDesc desc) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException;

    String genClass(ClassDesc desc) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
