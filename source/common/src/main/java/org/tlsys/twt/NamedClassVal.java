package org.tlsys.twt;

import org.tlsys.twt.links.ClassVal;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class NamedClassVal extends ClassVal implements Externalizable {
    private static final long serialVersionUID = -7463864458336802421L;
    private String className;

    public NamedClassVal(Class className) {
        this(className.getName());
    }

    public NamedClassVal(String className) {
        this.className = Objects.requireNonNull(className);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamedClassVal that = (NamedClassVal) o;

        return className.equals(that.className);

    }

    public String getClassName() {
        return className;
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(className);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        className = in.readUTF();
    }
}
