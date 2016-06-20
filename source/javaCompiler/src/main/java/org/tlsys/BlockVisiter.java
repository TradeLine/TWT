package org.tlsys;

import org.apache.bcel.generic.Type;
import org.tlsys.compiler.ast.*;
import org.tlsys.compiler.ast.Block;
import org.tlsys.compiler.ast.FieldWrite;
import org.tlsys.compiler.generators.NodeVisiter;
import org.tlsys.compiler.utils.Utils;
import org.tlsys.twt.FieldReferance;
import org.tlsys.twt.nodes.MethodReferance;
import org.tlsys.twt.nodes.SimpleClassReferance;
import org.tlsys.twt.nodes.code.*;
import org.tlsys.twt.nodes.code.FieldReadNode;

import java.util.Stack;

public class BlockVisiter implements NodeVisiter {
    private final int level;
    private final MethodVisiter methodVisiter;
    private Stack<CodeNode> cmd = new Stack<>();

    public BlockVisiter(int level, MethodVisiter methodVisiter) {
        this.level = level;
        this.methodVisiter = methodVisiter;
    }

    @Override
    public void visit(ASTNode node) {
        throw new RuntimeException("Unknown node " + node);
    }

    private void drawLevel(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("\t\t");
        }
        System.out.print(sb.toString());
    }

    public CodeNode getSingleCmd() {
        if (cmd.isEmpty()) {
            throw new IllegalStateException("Stack is empty!");
        }

        if (cmd.size() != 1)
            throw new IllegalStateException("Stack have more then one element!");

        return cmd.peek();
    }

    @Override
    public void visit(Block node) {
        drawLevel(level);
        System.out.println("{");
        if (level == 0) {
            for (VariableDeclaration vd : methodVisiter.getMethodDeclaration().getLocalVariables()) {
                drawLevel(level + 1);
                System.out.println("var " + vd.getName() + ";");
            }
        }
        ASTNode n = node.getFirstChild();
        int size = cmd.size();
        while (n != null) {
            if (n.getClass() == Block.class) {
                BlockVisiter bv = new BlockVisiter(level + 1, methodVisiter);
                drawLevel(level);
                n.visit(bv);
                n = n.getNextSibling();
                cmd.push(bv.getSingleCmd());
            } else {
                drawLevel(level + 1);
                n.visit(this);
                System.out.println("// LINE" + methodVisiter.getLineNumberCursor().getLineNumber(n));
                n = n.getNextSibling();
            }
        }
        size = cmd.size() - size;
        CodeNode[] nodes = new CodeNode[size];
        while (size > 0) {
            nodes[--size] = cmd.pop();
        }
        drawLevel(level);
        System.out.println("}");
        cmd.push(new CodeBlock(nodes));
    }

    @Override
    public void visit(MethodInvocation node) {
        if (node.getExpression() == null) {
            System.out.print(node.getMethodBinding().getDeclaringClass().getClassName() + ".");
        }
        Value self = null;
        if (node.getExpression() != null) {
            node.getExpression().visit(this);
            self = (Value) cmd.pop();
            System.out.print(".");
        }

        System.out.print(node.getMethodBinding().getName());
        if (node.isSpecial) {
            System.out.print(":" + node.getMethodBinding().getDeclaringClass().getClassName());
        }
        System.out.print("(");
        boolean first = true;
        CodeNode[] arguments = new CodeNode[node.getArguments().size()];
        int i = 0;
        for (ASTNode n : node.getArguments()) {
            if (!first) {
                System.out.print(", ");
            } else
                first = false;
            n.visit(this);
            arguments[i++] = cmd.pop();
        }
        System.out.print(")");

        cmd.push(new CodeInvoke(
                new MethodReferance(Utils.getReferanceBySignatyre(node.getMethodBinding().getDeclaringClass().getSignature()), node.getMethodBinding().getName(), node.getMethodBinding().getSignature())
                , arguments, self, node.isSpecial ? CodeInvoke.Type.SPECIAL : CodeInvoke.Type.DYNAMIC));
    }

    @Override
    public void visit(VariableBinding node) {
        System.out.print(node.getName());
        cmd.push(new LocalAccess(Utils.getReferance(node.getType())));
    }

    @Override
    public void visit(ArrayCreation node) {
        System.out.print("visit(ArrayCreation node) " + node);
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(ArrayAccess node) {
        if (node.getChildCount() < 2)
            throw new RuntimeException("!!! ERROR !!!");
        ASTNode el = node.getFirstChild();
        el.visit(this);
        el = el.getNextSibling();
        {

            System.out.print("[");
            el.visit(this);
            System.out.print("]");
            el = el.getNextSibling();
        }
        while (el != null) ;
    }

    @Override
    public void visit(ThisExpression node) {
        cmd.push(new ThisNode((SimpleClassReferance) Utils.getReferance(node.getVariableDeclaration().getType())));
    }

    @Override
    public void visit(DoStatement node) {
        System.out.print("visit(DoStatement node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(WhileStatement node) {
        System.out.print("visit(WhileStatement node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(IfStatement node) {
        System.out.print("if(");
        node.getExpression().visit(this);
        System.out.print(")");
        if (node.getIfBlock() != null)
            node.getIfBlock().visit(this);
        if (node.getElseBlock() != null) {
            System.out.print("else");
            node.getElseBlock().visit(this);
        }
        System.out.print("visit(IfStatement node)");
        //throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(TryStatement node) {
        System.out.print("visit(TryStatement node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(InfixExpression node) {
        if (node.getChildCount() != 2)
            throw new RuntimeException("!!! ERROR !!!");
        node.getFirstChild().visit(this);
        Value left = (Value) cmd.pop();
        node.getLastChild().visit(this);
        Value right = (Value) cmd.pop();

        InfixNode.Type op = null;

        if (node.getOperator() == InfixExpression.Operator.CONDITIONAL_AND) op = InfixNode.Type.CONDITIONAL_AND;
        if (node.getOperator() == InfixExpression.Operator.CONDITIONAL_OR) op = InfixNode.Type.CONDITIONAL_OR;
        if (node.getOperator() == InfixExpression.Operator.PLUS) op = InfixNode.Type.PLUS;
        if (node.getOperator() == InfixExpression.Operator.MINUS) op = InfixNode.Type.MINUS;
        if (node.getOperator() == InfixExpression.Operator.TIMES) op = InfixNode.Type.TIMES;
        if (node.getOperator() == InfixExpression.Operator.DIVIDE) op = InfixNode.Type.DIVIDE;
        if (node.getOperator() == InfixExpression.Operator.REMAINDER) op = InfixNode.Type.REMAINDER;
        if (node.getOperator() == InfixExpression.Operator.XOR) op = InfixNode.Type.XOR;
        if (node.getOperator() == InfixExpression.Operator.AND) op = InfixNode.Type.AND;
        if (node.getOperator() == InfixExpression.Operator.OR) op = InfixNode.Type.OR;
        if (node.getOperator() == InfixExpression.Operator.EQUALS) op = InfixNode.Type.EQUALS;
        if (node.getOperator() == InfixExpression.Operator.NOT_EQUALS) op = InfixNode.Type.NOT_EQUALS;
        if (node.getOperator() == InfixExpression.Operator.GREATER_EQUALS) op = InfixNode.Type.GREATER_EQUALS;
        if (node.getOperator() == InfixExpression.Operator.GREATER) op = InfixNode.Type.GREATER;
        if (node.getOperator() == InfixExpression.Operator.LESS_EQUALS) op = InfixNode.Type.LESS_EQUALS;
        if (node.getOperator() == InfixExpression.Operator.LESS) op = InfixNode.Type.LESS;
        if (node.getOperator() == InfixExpression.Operator.RIGHT_SHIFT_SIGNED) op = InfixNode.Type.RIGHT_SHIFT_SIGNED;
        if (node.getOperator() == InfixExpression.Operator.LEFT_SHIFT) op = InfixNode.Type.LEFT_SHIFT;
        if (node.getOperator() == InfixExpression.Operator.RIGHT_SHIFT_UNSIGNED)
            op = InfixNode.Type.RIGHT_SHIFT_UNSIGNED;
        if (op == null)
            throw new RuntimeException("Not supported operation");

        cmd.push(new InfixNode(left, right, op));

    }

    @Override
    public void visit(PrefixExpression node) {
        System.out.print("visit(PrefixExpression node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(PostfixExpression node) {
        System.out.print("visit(PostfixExpression node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(SwitchStatement node) {
        System.out.print("visit(SwitchStatement node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(SwitchCase node) {
        System.out.print("visit(SwitchCase node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(CatchClause node) {
        System.out.print("visit(CatchClause node)");
        throw new RuntimeException("Not supported yet!");
    }


    @Override
    public void visit(StringLiteral node) {
        //System.out.print("visit(StringLiteral node)");
        System.out.print("\"" + node.getValue() + "\"");
        cmd.push(new StringNode(node.getValue()));
    }

    @Override
    public void visit(ClassLiteral node) {
        System.out.print(node.getSignature().className() + ".class");

        cmd.push(new ClassAccess(node.getReferance()));
    }

    @Override
    public void visit(NullLiteral node) {
        System.out.print("visit(NullLiteral node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(ClassInstanceCreation node) {
        System.out.print("new " + node.getTypeBinding().getSignature());
        System.out.print("(");
        boolean first = true;
        for (ASTNode n : node.getArguments()) {
            if (!first)
                System.out.print(", ");
            else
                first = false;
            n.visit(this);
        }
        System.out.print(")");
        cmd.push(new ClassNew(Utils.getReferance(node.getTypeBinding())));
    }

    @Override
    public void visit(ArrayInitializer node) {
        System.out.print("visit(ArrayInitializer node)");
        throw new RuntimeException("Not supported yet!");
    }


    @Override
    public void visit(VariableDeclaration node) {
        System.out.print("visit(VariableDeclaration node)");
        throw new RuntimeException("Not supported yet!");
    }


    @Override
    public void visit(FieldAccess node) {
        Value self = null;
        if (node.getExpression() == null)
            System.out.print(node.getType().getSignature());
        else {
            node.getExpression().visit(this);
            self = (Value) cmd.pop();
        }
        Expression e = node.getExpression();
        System.out.print("." + node.getName() + e);


        if (node instanceof FieldRead) {
            cmd.push(new FieldReadNode(self, new FieldReferance(Utils.getReferanceBySignatyre(node.getType().getSignature()), node.getName())));
            return;
        }

        if (node instanceof FieldWrite) {
            cmd.push(new org.tlsys.twt.nodes.code.FieldWrite(self, new FieldReferance(Utils.getReferanceBySignatyre(node.getType().getSignature()), node.getName())));
            return;
        }

        throw new RuntimeException("Not supported yet");
    }

    @Override
    public void visit(BreakStatement node) {
        System.out.print("break");
        if (node != null)
            System.out.println(" " + node.getLabel());
        //throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(ContinueStatement node) {
        System.out.print("visit(ContinueStatement node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(CastExpression node) {
        System.out.print("visit(CastExpression node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(BooleanLiteral node) {
        System.out.print("visit(BooleanLiteral node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(ThrowStatement node) {
        System.out.print("visit(ThrowStatement node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(Name node) {
        System.out.print("visit(Name node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(InstanceofExpression node) {
        System.out.print("visit(InstanceofExpression node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(ConditionalExpression node) {
        System.out.print("visit(ConditionalExpression node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(SynchronizedBlock node) {
        System.out.print("visit(SynchronizedBlock node)");
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public void visit(PrimitiveCast node) {
        node.getExpression().visit(this);
        cmd.push(new PrimitiveCastNode(Utils.getReferance(node.getType()), (Value) cmd.pop()));
    }

    @Override
    public void visit(ReturnStatement node) {
        if (node.getExpression() != null) {
            node.getExpression().visit(this);
            cmd.push(new Return(cmd.pop()));
        } else
            cmd.push(new Return());
    }

    @Override
    public void visit(Assignment node) {
        if (node.getChildCount() != 2)
            throw new RuntimeException("!!! ERROR !!!");
        node.getFirstChild().visit(this);
        Value left = (Value) cmd.pop();

        System.out.print(node.getOperator().toString());
        node.getLastChild().visit(this);
        Value right = (Value) cmd.pop();

        AssignmentNode.Type op = null;

        if (node.getOperator() == Assignment.Operator.ASSIGN) op = AssignmentNode.Type.ASSIGN;
        if (node.getOperator() == Assignment.Operator.PLUS_ASSIGN) op = AssignmentNode.Type.PLUS_ASSIGN;
        if (node.getOperator() == Assignment.Operator.MINUS_ASSIGN) op = AssignmentNode.Type.MINUS_ASSIGN;
        if (node.getOperator() == Assignment.Operator.TIMES_ASSIGN) op = AssignmentNode.Type.TIMES_ASSIGN;
        if (node.getOperator() == Assignment.Operator.DIVIDE_ASSIGN) op = AssignmentNode.Type.DIVIDE_ASSIGN;
        if (node.getOperator() == Assignment.Operator.BIT_AND_ASSIGN) op = AssignmentNode.Type.BIT_AND_ASSIGN;
        if (node.getOperator() == Assignment.Operator.BIT_OR_ASSIGN) op = AssignmentNode.Type.BIT_OR_ASSIGN;
        if (node.getOperator() == Assignment.Operator.BIT_XOR_ASSIGN) op = AssignmentNode.Type.BIT_XOR_ASSIGN;
        if (node.getOperator() == Assignment.Operator.REMAINDER_ASSIGN) op = AssignmentNode.Type.REMAINDER_ASSIGN;
        if (node.getOperator() == Assignment.Operator.LEFT_SHIFT_ASSIGN) op = AssignmentNode.Type.LEFT_SHIFT_ASSIGN;
        if (node.getOperator() == Assignment.Operator.RIGHT_SHIFT_SIGNED_ASSIGN)
            op = AssignmentNode.Type.RIGHT_SHIFT_SIGNED_ASSIGN;
        if (node.getOperator() == Assignment.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN)
            op = AssignmentNode.Type.RIGHT_SHIFT_UNSIGNED_ASSIGN;
        if (op == null)
            throw new RuntimeException("Unknown operator!");
        cmd.push(new AssignmentNode(left, right, op));
    }



    @Override
    public void visit(NumberLiteral node) {
        if (node.getType() == Type.INT) {
            cmd.push(new IntNode(node.getValue().intValue()));
            return;
        }

        if (node.getType() == Type.BYTE) {
            cmd.push(new ByteNode(node.getValue().byteValue()));
            return;
        }


        throw new RuntimeException("Not supported yet!");
    }
}
