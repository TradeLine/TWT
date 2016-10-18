package org.tlsys.twt;

public interface ClassAliaseProvider {
    Class getReplacedClass(Class clazz);
    Class getRealClass(Class clazz);
    void addClass(Class orign, Class real);
}
