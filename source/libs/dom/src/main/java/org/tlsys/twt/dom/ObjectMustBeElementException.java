package org.tlsys.twt.dom;

import org.tlsys.twt.annotations.JSClass;

@JSClass
public class ObjectMustBeElementException extends RuntimeException {
    public ObjectMustBeElementException() {
    }

    public ObjectMustBeElementException(String message) {
        super(message);
    }
}
