package org.tlsys.twt.nodes.code;


public interface CodeVisiter {
    public default void visit(CodeNode node) {
        //
    }

    public default void visit(CodeBlock block) {
        visit((CodeNode)block);
    }
    public default void visit(CodeInvoke invoke) {
        visit((CodeNode)invoke);
    }

    public default void visit(ThisNode node) {
        visit((CodeNode)node);
    }

    public default void visit(LocalAccess local) {
        visit((CodeNode)local);
    }

    public default void visit(ClassAccess node) {
        visit((CodeNode) node);
    }

    public default void visit(FieldAccessNode fieldAccess) {
        visit((CodeNode)fieldAccess);
    }

    public default void visit(ConstNode node) {
        visit((CodeNode)node);
    }
    public default void visit(AssignmentNode assignment) {
        visit((CodeNode)assignment);
    }
    public default void visit(ClassNew classNew) {
        visit((CodeNode)classNew);
    }

    public default void visit(Return aReturn) {
        visit((CodeNode)aReturn);
    }

    public default void visit(PrimitiveCastNode cast) {
        visit((CodeNode)cast);
    }
}
