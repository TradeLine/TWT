package org.tlsys.wildfly.exceptions;

public class ResourceNotFoundException extends WildFlyException {
    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
