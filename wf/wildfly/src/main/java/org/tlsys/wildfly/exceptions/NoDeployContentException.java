package org.tlsys.wildfly.exceptions;

/**
 * Created by caffeine on 15.02.2016.
 */
public class NoDeployContentException extends WildFlyException {
    public NoDeployContentException() {
    }

    public NoDeployContentException(String message) {
        super(message);
    }
}
