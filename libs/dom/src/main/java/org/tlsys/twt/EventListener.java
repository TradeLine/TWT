package org.tlsys.twt;

import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.MethodAlias;

@JSClass
@FunctionalInterface
public interface EventListener {
    @MethodAlias("onEvent")
    public void onEvent(Object sender, Event event);
}
