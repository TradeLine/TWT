package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;

/**
 * Created by Субочев Антон on 20.06.2016.
 */
public class IntNode extends NumberNode {

    private final int value;

    public IntNode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int getInteger() {
        return getValue();
    }

    @Override
    public double getDouble() {
        return value;
    }

    @Override
    public ClassReferance getResultType() {
        return null;
    }
}
