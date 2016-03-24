package org.tlsys.twt.rt.java.util;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@JSClass
@ClassName("java.util.ArrayList")
public class TArrayList<E> implements List<E> {

    private Object jsArray;

    public TArrayList(Object jsArray) {
        this.jsArray = jsArray;
    }

    public TArrayList() {
        jsArray = Script.code("[]");
    }

    public TArrayList(int initialCapacity) {
        this();
    }

    public TArrayList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    @Override
    public int size() {
        return CastUtil.toInt(Script.code(jsArray, ".length"));
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public Object[] toArray() {
        Object[] out = new Object[size()];
        for (int i = 0; i < size(); i++)
            out[i] = get(i);
        return out;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new NullPointerException("Not supported yet");
    }

    @Override
    public boolean add(E e) {
        Script.code(jsArray, ".push(", e, ")");
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index == -1)
            return false;
        remove(index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c)
            if (!contains(o))
                return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean a = false;
        int b = 0;
        for (E e : c) {
            a = add(e) || a;
            b++;
        }
        return a;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new NullPointerException("Not supported yet");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new NullPointerException("Not supported yet");
    }

    @Override
    public void clear() {
        jsArray = Script.code("[]");
    }

    @Override
    public E get(int index) {
        return Script.code(jsArray, "[", CastUtil.toObject(index), "]");
    }

    @Override
    public E set(int index, E element) {
        Script.code(jsArray, "[", CastUtil.toObject(index), "]=", element);
        return element;
    }

    @Override
    public void add(int index, E element) {
        Script.code(jsArray, ".splice(", CastUtil.toObject(index), ",0,", element, ")");
    }

    @Override
    public E remove(int index) {
        E e = get(index);
        Script.code(jsArray, ".splice(", CastUtil.toObject(index), ",1)");
        return e;
    }

    @Override
    public int indexOf(Object o) {
        return CastUtil.toInt(Script.code(jsArray, ".indexOf(", o, ")"));
    }

    @Override
    public int lastIndexOf(Object o) {
        return CastUtil.toInt(Script.code(jsArray, ".lastIndexOf(", o, ")"));
    }

    @Override
    public ListIterator<E> listIterator() {
        return new IteratorImp();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new IteratorImp(index - 1);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        TArrayList aa = new TArrayList<E>();
        aa.jsArray = Script.code(jsArray, ".slice(", CastUtil.toObject(fromIndex), ",", CastUtil.toObject(toIndex), ")");
        return aa;
    }

    private class IteratorImp implements ListIterator<E> {
        private int index;

        public IteratorImp(int index) {
            this.index = index;
        }

        public IteratorImp() {
            this(-1);
        }

        @Override
        public boolean hasNext() {
            return nextIndex() < TArrayList.this.size();
        }

        @Override
        public E next() {
            E e = TArrayList.this.get(++index);
            return e;
        }

        @Override
        public boolean hasPrevious() {
            return previousIndex() >= 0;
        }

        @Override
        public E previous() {
            return TArrayList.this.get(--index);
        }

        @Override
        public int nextIndex() {
            int o = index + 1;
            return o;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public void remove() {
            TArrayList.this.remove(index--);
        }

        @Override
        public void set(E e) {
            TArrayList.this.set(index, e);
        }

        @Override
        public void add(E e) {
            TArrayList.this.add(index, e);
        }
    }
}
