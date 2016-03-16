package org.tlsys;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.JSClass;

@JSClass
public class Parent<T> {
    public void doit(T val) {
        Script.code("console.info('Parent::doit')");
    }
}
