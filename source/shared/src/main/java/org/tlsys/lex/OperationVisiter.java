package org.tlsys.lex;

import org.tlsys.lex.declare.VBlock;
import org.tlsys.twt.CompileException;

public interface OperationVisiter {
    boolean visit(Operation node) throws CompileException;

    default boolean visit(VIf node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(VBinar node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(Return node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(WhileLoop node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(Try node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(Throw node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(This node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(Switch node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(StaticRef node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(SetValue node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(SetField node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(Parens node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(NewClass node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(NewArrayLen node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(NewArrayItems node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(Label node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(InstanceOf node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(Increment node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(GetValue node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(GetField node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(FunctionRef node) throws CompileException {
        return visit((Value) node);
    }


    default boolean visit(ForLoop node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(DoWhileLoop node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(Continue node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(Const node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(Conditional node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(ClassRef node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(Break node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(Assign node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(ArrayGet node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(ArrayAssign node) throws CompileException {
        return visit((Value) node);
    }

    default boolean visit(Invoke node) throws CompileException {
        return visit((Value) node);
    }
    default boolean visit(VBlock node) throws CompileException {
        return visit((Operation) node);
    }

    default boolean visit(Line node) throws CompileException {
        return visit((Operation) node);
    }
    default boolean visit(Value node) throws CompileException {
        return visit((Operation) node);
    }





}
