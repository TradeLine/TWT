package org.tlsys;

import org.tlsys.compiler.ast.*;
import org.tlsys.compiler.graph.*;
import org.tlsys.compiler.generators.NodeVisiter;
import org.tlsys.twt.nodes.SimpleClassReferance;
import org.tlsys.twt.nodes.SimpleTClass;
import org.tlsys.twt.nodes.TClass;
import org.tlsys.twt.nodes.TMethod;

import java.util.Objects;

public class TWTClassVisiter implements NodeVisiter {
    @Override
    public void visit(ASTNode node) {
        System.out.println("visit(ASTNode node) " + node);
    }

    @Override
    public void visit(TypeDeclaration node) {
        System.out.println("visit(TypeDeclaration node)");
        TMethod[] methods = new TMethod[node.getMethods().length];
        int i = 0;
        for (MethodDeclaration md : node.getMethods()) {
            MethodVisiter methodVisiter = new MethodVisiter();
            methodVisiter.visit(md);
            methods[i++] = Objects.requireNonNull(methodVisiter.getMethod(), "Parsed method is NULL");
        }
        result = new SimpleTClass(node.getClassName(), methods, new SimpleClassReferance(node.getSuperType().getClassName()), new SimpleClassReferance[0]);
        for (TMethod m : methods)
            m.setParent(result);
    }

    private TClass result;

    public TClass getResult() {
        return result;
    }

    @Override
    public void visit(MethodDeclaration node) {

    }


}
