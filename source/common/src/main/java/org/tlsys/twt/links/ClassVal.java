package org.tlsys.twt.links;

import org.tlsys.twt.ArrayClassVal;

import java.io.Serializable;

public abstract class ClassVal implements Serializable {
    private static final long serialVersionUID = -9104573463212618303L;

    private ArrayClassVal array = null;

    public ArrayClassVal asArray() {
        if (array == null)
            array = new ArrayClassVal(this);
        return array;
    }
}
