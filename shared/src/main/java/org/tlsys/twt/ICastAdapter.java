package org.tlsys.twt;

import com.sun.tools.javac.tree.JCTree;

public interface ICastAdapter {
    public String cast(GenContext context, Class from, Class to, JCTree.JCExpression value) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException;
}
