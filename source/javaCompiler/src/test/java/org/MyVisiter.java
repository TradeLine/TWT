package org;

import org.tlsys.compiler.ast.*;
import org.tlsys.compiler.graph.*;
import org.tlsys.compiler.generators.NodeVisiter;

public class MyVisiter implements NodeVisiter {
    @Override
    public void visit(ASTNode node) {
        System.out.println("visit(ASTNode node) " + node);
    }

    @Override
    public void visit(TypeDeclaration node) {
        System.out.println("visit(TypeDeclaration node)");
        for (MethodDeclaration md : node.getMethods()) {
            visit(md);
        }
    }

    private final MethodVisiter methodVisiter = new MethodVisiter();

    @Override
    public void visit(MethodDeclaration node) {
        methodVisiter.visit(node);
    }


}
