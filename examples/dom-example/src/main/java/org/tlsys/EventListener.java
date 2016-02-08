package org.tlsys;

import org.tlsys.twt.annotations.JSClass;

@JSClass
@FunctionalInterface
public interface EventListener {
    public void onEvent(Object sender);
}
