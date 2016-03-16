package org.tlsys.twt;

public interface ClassAliaseProvider {
    public Class getReplacedClass(Class clazz);
    public Class getRealClass(Class clazz);
    public void addClass(Class orign, Class real);
}
