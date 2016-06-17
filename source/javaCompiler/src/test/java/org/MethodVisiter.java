package org;

import org.tlsys.compiler.generators.NodeVisiter;
import org.tlsys.compiler.parser.LineNumberCursor;
import org.tlsys.compiler.ast.*;
import org.tlsys.compiler.graph.*;
import java.lang.reflect.Modifier;

public class MethodVisiter implements NodeVisiter {
    private MethodDeclaration methodDeclaration;

    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }

    @Override
    public void visit(ASTNode node) {

    }

    private LineNumberCursor lineNumberCursor;

    public LineNumberCursor getLineNumberCursor() {
        return lineNumberCursor;
    }

    @Override
    public void visit(MethodDeclaration node) {
        methodDeclaration = node;
        lineNumberCursor = new LineNumberCursor(node.getCode());
        System.out.println("\n\n");
        //System.out.println("visit(MethodDeclaration node)");
        if (Modifier.isPublic(node.getAccess()))
            System.out.print("public ");

        if (Modifier.isPrivate(node.getAccess()))
            System.out.print("private ");

        if (Modifier.isProtected(node.getAccess()))
            System.out.print("protected ");

        if (Modifier.isStatic(node.getAccess()))
            System.out.print("static ");

        if (Modifier.isFinal(node.getAccess()))
            System.out.print("final ");

        System.out.print(node.getMethodBinding().getName());
        System.out.print("(");
        boolean first = true;
        for (VariableDeclaration vd : node.getParameters()) {
            if (!first) {
                System.out.print(", ");
            } else
                first = false;
            System.out.print(vd.getType().getSignature() + " ");
            System.out.print(vd.getName());
        }
        System.out.print(")");
        node.getBody().visit(this);
        System.out.println("\n\n");
    }

    @Override
    public void visit(Block node) {
        BlockVisiter blockVisiter = new BlockVisiter(0, this);
        blockVisiter.visit(node);
    }




}
