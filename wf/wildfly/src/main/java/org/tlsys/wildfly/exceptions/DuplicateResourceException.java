package org.tlsys.wildfly.exceptions;

/**
 * Created by caffeine on 15.02.2016.
 */
public class DuplicateResourceException extends WildFlyException {
    public DuplicateResourceException() {
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
