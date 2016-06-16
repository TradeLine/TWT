package org;

import jdk.nashorn.internal.ir.*;
import jdk.nashorn.internal.ir.Assignment;
import org.tlsys.ast.*;
import org.tlsys.ast.Block;
import org.tlsys.generators.NodeVisiter;

import java.lang.reflect.Modifier;

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
