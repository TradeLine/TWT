package org.tlsys.lex.declare;

import org.tlsys.sourcemap.SourcePoint;
import org.tlsys.twt.CompileException;

public class VFieldNotFoundException extends CompileException {
    private static final long serialVersionUID = 1254771473514515152L;
    private VClass clazz;
    private String fieldName;

    public VFieldNotFoundException(VClass clazz, String fieldName, SourcePoint point) {
        super(point);
        this.clazz = clazz;
        this.fieldName = fieldName;
    }

    public VClass getClazz() {
        return clazz;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getMessage() {
        return getClazz() + "::" + getFieldName();
    }
}
