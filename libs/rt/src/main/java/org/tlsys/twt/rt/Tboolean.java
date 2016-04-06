package org.tlsys.twt.rt;

import org.tlsys.twt.annotations.CastAdapter;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.rt.java.lang.BoxingCast;

@JSClass
@ClassName(value = "boolean", primitive = true, nativeName = "Z")
@CastAdapter(BoxingCast.class)
public class Tboolean {
}
