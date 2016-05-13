package org.tlsys.twt;

import org.tlsys.twt.links.ClassVal;

import java.util.Objects;

public class ArrayClassVal extends ClassVal {
    private static final long serialVersionUID = -4463723829262809786L;
    private final ClassVal ref;

    public ArrayClassVal(ClassVal ref) {
        this.ref = Objects.requireNonNull(ref);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrayClassVal that = (ArrayClassVal) o;

        return ref.equals(that.ref);

    }

    @Override
    public int hashCode() {
        return ref.hashCode();
    }
}
