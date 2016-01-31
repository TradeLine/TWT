package org.tlsys.twt.dom;

import org.tlsys.twt.Script;

import java.util.Objects;

public final class Elements {
    private Elements() {
    }

    public static <T> T requireElement(T object) {
        if (Script.code(object, " instanceof HTMLElement")) {
            return object;
        }

        throw new ObjectMustBeElementException();
    }

    public static <T> T requireElement(T object, String text) {
        if (Script.code(object, " instanceof HTMLElement")) {
            return object;
        }

        throw new ObjectMustBeElementException(text);
    }

}
