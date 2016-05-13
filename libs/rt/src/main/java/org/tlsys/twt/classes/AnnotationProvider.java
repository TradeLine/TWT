package org.tlsys.twt.classes;

import org.tlsys.twt.NativeCodeGenerator;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.java.lang.annotation.TAnnotation;

@JSClass
@CodeGenerator(NativeCodeGenerator.class)
public interface AnnotationProvider {
    public TAnnotation getAnnotation();
}
