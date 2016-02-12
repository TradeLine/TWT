package org.tlsys.twt.rt.java.util;

import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

import java.util.Collection;
import java.util.Iterator;

@JSClass
@ClassName("java.util.Set")
public interface TSet<E> extends Collection<E> {
    int size();
    boolean isEmpty();
    boolean contains(Object o);
    Iterator<E> iterator();
    Object[] toArray();
    <T> T[] toArray(T[] a);
    boolean add(E e);
    boolean remove(Object o);
    boolean containsAll(Collection<?> c);
    boolean addAll(Collection<? extends E> c);
    boolean retainAll(Collection<?> c);
    boolean removeAll(Collection<?> c);
    void clear();
    boolean equals(Object o);
    int hashCode();
    /*
    @Override
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.DISTINCT);
    }
    */
}
