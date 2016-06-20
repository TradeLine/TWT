package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;

/**
 * Created by Субочев Антон on 20.06.2016.
 */
public class ByteNode extends NumberNode {
    private final byte value;

    public ByteNode(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    @Override
    public byte getByte() {
        return getValue();
    }

    @Override
    public double getDouble() {
        return getValue();
    }

    @Override
    public ClassReferance getResultType() {
        return null;
    }
}
