package org.tlsys.twt.rt.java.util;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

import java.util.Collection;
import java.util.Iterator;

@JSClass
@ClassName("java.util.Collection")
public interface TCollection<E> extends Iterable<E> {
    public Iterator<E> iterator();
    public boolean add(E e);
    public boolean addAll(Collection<? extends E> c);
    public void clear();
    public boolean contains(Object o);
    public boolean containsAll(Collection<?> c);
    public boolean isEmpty();
    boolean remove(Object o);
    boolean removeAll(Collection<?> c);
    boolean retainAll(Collection<?> c);
    public int size();
    public Object[] toArray();
    public <T> T[] toArray(T[] a);
}
