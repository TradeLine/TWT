package org.tlsys.twt.classes;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public class ArrayConstructor {
    public Class component;

    private ArrayConstructor(Class component) {
        this.component = component;
    }


}
