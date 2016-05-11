package org.tlsys.twt.rt.java.lang.annotation;

import org.tlsys.twt.annotations.ReplaceClass;

import java.lang.annotation.Annotation;

@ReplaceClass(Annotation.class)
public interface TAnnotation {
    boolean equals(Object obj);

    int hashCode();

    String toString();

    public Class<? extends Annotation> annotationType();
}
