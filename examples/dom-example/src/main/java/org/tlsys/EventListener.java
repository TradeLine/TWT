package org.tlsys;

@FunctionalInterface
public interface EventListener {
    public void onEvent(Object sender);
}
