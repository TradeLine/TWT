package org.tlsys;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Parent {
    public void doit() {
        Script.code("console.info('Parent::doit')");
    }
}
