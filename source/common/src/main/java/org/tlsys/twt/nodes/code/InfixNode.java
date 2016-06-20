package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;

/**
 * Created by Субочев Антон on 20.06.2016.
 */
public class InfixNode extends Value {
    private final Value left;
    private final Value right;
    private final Type type;

    public InfixNode(Value left, Value right, Type type) {
        this.left = left;
        this.right = right;
        this.type = type;
    }

    @Override
    public ClassReferance getResultType() {
        return null;
    }

    @Override
    public void accept(CodeVisiter visiter) {

    }

    public enum Type {
        CONDITIONAL_AND("&&"),
        CONDITIONAL_OR("||"),
        PLUS("+"),
        MINUS("-"),
        TIMES("*"),
        DIVIDE("/"),
        REMAINDER("%"),
        XOR("^"),
        AND("&"),
        OR("|"),
        EQUALS("=="),
        NOT_EQUALS("!="),
        GREATER_EQUALS(">="),
        GREATER(">"),
        LESS_EQUALS("<="),
        LESS("<"),
        RIGHT_SHIFT_SIGNED(">>"),
        LEFT_SHIFT("<<"),
        RIGHT_SHIFT_UNSIGNED(">>>");

        private final String text;
        Type(String text) {
            this.text = text;
        }
    }
}
