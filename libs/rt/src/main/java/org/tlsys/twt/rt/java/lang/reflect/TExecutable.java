package org.tlsys.twt.rt.java.lang.reflect;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.twt.classes.MethodRecord;
import org.tlsys.twt.rt.java.lang.TClass;
import org.tlsys.twt.rt.java.lang.annotation.TAnnotation;

@JSClass
@ReplaceClass(java.lang.reflect.Executable.class)
public abstract class TExecutable {

    private final TClass parentClass;
    private final MethodRecord record;
    private TAnnotation[] annotations;

    public TExecutable(TClass parentClass, MethodRecord record) {
        this.parentClass = parentClass;
        this.record = record;
    }

    public MethodRecord getRecord() {
        return record;
    }

    public Class<?> getDeclaringClass() {
        return CastUtil.cast(parentClass);
    }
    public abstract String getName();
    public int getParameterCount() {
        return getRecord().getArguments().length();
    }


    public TAnnotation[] getDeclaredAnnotations() {
        if (annotations != null)
            return annotations;

        annotations = new TAnnotation[record.getAnnotations().length()];
        for (int i = 0; i < record.getAnnotations().length(); i++) {
            annotations[i] = record.getAnnotations().get(i).getAnnotation();
        }
        return annotations;
    }


    public <T extends TAnnotation> T getAnnotation(Class<T> annotationClass) {
        for (TAnnotation a : getDeclaredAnnotations()) {
            if (a.getClass() == annotationClass)
                return CastUtil.cast(a);
        }
        return null;
    }
}
