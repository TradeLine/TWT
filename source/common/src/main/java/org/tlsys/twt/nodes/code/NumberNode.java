package org.tlsys.twt.nodes.code;

import org.tlsys.twt.nodes.ClassReferance;

public abstract class NumberNode extends ConstNode {
    public float getFloat() {
        return getInteger();
    }

    public abstract double getDouble();

    public int getInteger() {
        return (int)getLong();
    }

    public long getLong() {
        return (long)getDouble();
    }
    public byte getByte() {
        return (byte)getShort();
    }
    public short getShort() {
        return (short)getInteger();
    }
}
