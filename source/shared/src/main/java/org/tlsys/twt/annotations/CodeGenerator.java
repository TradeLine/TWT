package org.tlsys.twt.annotations;

import org.tlsys.twt.ICodeGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD,ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeGenerator {
    Class<? extends ICodeGenerator> value();
}
