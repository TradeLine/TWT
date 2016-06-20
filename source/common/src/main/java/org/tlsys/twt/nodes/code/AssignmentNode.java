package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;

/**
 * Created by Субочев Антон on 20.06.2016.
 */
public class AssignmentNode extends Value {
    private final Value left;
    private final Value right;
    private final Type type;

    public AssignmentNode(Value left, Value right, Type type) {
        this.left = left;
        this.right = right;
        this.type = type;
    }

    @Override
    public ClassReferance getResultType() {
        return left.getResultType();
    }

    @Override
    public void accept(CodeVisiter visiter) {
        visiter.visit(this);
    }

    public enum Type {
        ASSIGN("="),
        PLUS_ASSIGN("+="),
        MINUS_ASSIGN("-="),
        TIMES_ASSIGN("*="),
        DIVIDE_ASSIGN("/="),
        BIT_AND_ASSIGN("&="),
        BIT_OR_ASSIGN("|="),
        BIT_XOR_ASSIGN("^="),
        REMAINDER_ASSIGN("%="),
        LEFT_SHIFT_ASSIGN("<<="),
        RIGHT_SHIFT_SIGNED_ASSIGN(">>="),
        RIGHT_SHIFT_UNSIGNED_ASSIGN(">>>=");

        Type(String value) {
        }
    }
}