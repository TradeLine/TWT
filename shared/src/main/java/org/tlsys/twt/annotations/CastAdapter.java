package org.tlsys.twt.annotations;

import org.tlsys.twt.ICastAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CastAdapter {
    Class<? extends ICastAdapter> value();
}
